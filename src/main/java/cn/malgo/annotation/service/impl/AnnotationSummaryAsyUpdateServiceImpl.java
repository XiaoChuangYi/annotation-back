package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationEvaluateStateEnum;
import cn.malgo.annotation.service.AnnotationSummaryAsyncUpdateService;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AnnotationSummaryAsyUpdateServiceImpl implements AnnotationSummaryAsyncUpdateService {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskDocRepository annotationTaskDocRepository;

  private final AnnotationCombineRepository annotationCombineRepository;

  public AnnotationSummaryAsyUpdateServiceImpl(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationTaskDocRepository annotationTaskDocRepository,
      final AnnotationCombineRepository annotationCombineRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskDocRepository = annotationTaskDocRepository;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  /** 0 0/5 * * * ? 秒 分 时 天(月) 月 天(星期) 天(月),天(星期)互斥，任意一个为? 当前的corn表达式的意思时每隔5分钟触发一次 */
  @Override
  @Scheduled(cron = "0 0/1 * * * ?")
  @Transactional
  public List<AnnotationTask> asyncUpdateAnnotationOverview() {
    try {
      final List<AnnotationTask> annotationTasks = annotationTaskRepository.findAll();
      log.info("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
      IntStream.range(0, annotationTasks.size())
          .forEach(
              i -> {
                final AnnotationTask annotationTask = annotationTasks.get(i);
                final List<AnnotationCombine> annotationCombines =
                    annotationCombineRepository.findAllByBlockIdIn(
                        getBlocksId(annotationTask.getId()));
                annotationTask.setTotalBranchNum(
                    getBranchNum(annotationCombines, AnnotationEvaluateStateEnum.total_branch));

                annotationTask.setAnnotatedBranchNum(
                    getBranchNum(annotationCombines, AnnotationEvaluateStateEnum.annotated_branch));

                annotationTask.setRestBranchNum(
                    getBranchNum(annotationCombines, AnnotationEvaluateStateEnum.rest_branch));

                annotationTask.setAbandonBranchNum(
                    getBranchNum(annotationCombines, AnnotationEvaluateStateEnum.abandon_branch));

                annotationTask.setTotalWordNum(
                    getWordNum(annotationCombines, AnnotationEvaluateStateEnum.total_word));

                annotationTask.setAnnotatedWordNum(
                    getWordNum(annotationCombines, AnnotationEvaluateStateEnum.annotated_word));

                annotationTask.setRestWordNum(
                    getWordNum(annotationCombines, AnnotationEvaluateStateEnum.rest_word));

                annotationTask.setAbandonWordNum(
                    getWordNum(annotationCombines, AnnotationEvaluateStateEnum.abandon_word));

                annotationTask.setInConformity(0);
              });
      return annotationTaskRepository.saveAll(annotationTasks);
    } catch (Exception ex) {
      log.info("定时任务，更新task表，发生异常:{}", ex.getMessage());
    }
    return null;
  }

  private List<Integer> getBlocksId(int taskId) {
    return annotationTaskDocRepository
        .findAllByTask(new AnnotationTask(taskId))
        .stream()
        .flatMap(annotationTaskDoc -> annotationTaskDoc.getBlocks().stream())
        .collect(Collectors.toList())
        .stream()
        .map(annotationTaskDocBlock -> annotationTaskDocBlock.getId().getBlockId())
        .collect(Collectors.toList());
  }

  private int getBranchNum(
      final List<AnnotationCombine> annotationCombines,
      final AnnotationEvaluateStateEnum annotationEvaluateStateEnum) {
    List<String> annotationStateList = Collections.emptyList();
    switch (annotationEvaluateStateEnum) {
      case total_branch: // 获取当前批次的总条数
        annotationStateList =
            Arrays.asList(
                AnnotationCombineStateEnum.preAnnotation.name(),
                AnnotationCombineStateEnum.annotationProcessing.name(),
                AnnotationCombineStateEnum.preExamine.name());
        break;
      case rest_branch: // 获取当前批次的剩下的条数
        annotationStateList =
            Arrays.asList(
                AnnotationCombineStateEnum.preAnnotation.name(),
                AnnotationCombineStateEnum.annotationProcessing.name());
        break;
      case annotated_branch: // 获取当前批次的已标注的条数
        annotationStateList = Arrays.asList(AnnotationCombineStateEnum.preExamine.name());
        break;
      case abandon_branch: // 获取当前批次放弃的条数
        annotationStateList = Arrays.asList(AnnotationCombineStateEnum.abandon.name());
        break;
    }
    if (annotationStateList.size() > 0) {
      final List<String> finalAnnotationStateList = annotationStateList;
      return annotationCombines
          .stream()
          .filter(
              annotationCombine -> finalAnnotationStateList.contains(annotationCombine.getState()))
          .collect(Collectors.toList())
          .size();
    }
    return 0;
  }

  private int getWordNum(
      final List<AnnotationCombine> annotationCombines,
      AnnotationEvaluateStateEnum annotationEvaluateStateEnum) {
    List<String> annotationStateList = Collections.emptyList();
    switch (annotationEvaluateStateEnum) {
      case total_word: // 获取当前批次的总字数
        annotationStateList =
            Arrays.asList(
                AnnotationCombineStateEnum.preAnnotation.name(),
                AnnotationCombineStateEnum.annotationProcessing.name(),
                AnnotationCombineStateEnum.preExamine.name());
        break;
      case annotated_word: // 获取当前批次的已标注的总字数
        annotationStateList = Arrays.asList(AnnotationCombineStateEnum.preExamine.name());
        break;
      case rest_word: // 获取当前批次剩余的字数
        annotationStateList =
            Arrays.asList(
                AnnotationCombineStateEnum.preAnnotation.name(),
                AnnotationCombineStateEnum.annotationProcessing.name());
        break;
      case abandon_word: // 获取当前批次放弃的字数
        annotationStateList = Arrays.asList(AnnotationCombineStateEnum.abandon.name());
        break;
    }
    if (annotationCombines.size() > 0) {
      final List<String> finalAnnotationStateList = annotationStateList;
      return annotationCombines
          .stream()
          .filter(
              annotationCombine -> finalAnnotationStateList.contains(annotationCombine.getState()))
          .mapToInt(value -> value.getTerm().length())
          .sum();
    }
    return 0;
  }
}
