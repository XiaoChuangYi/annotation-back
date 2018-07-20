package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationStaffEvaluateRepository;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationEvaluateStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.AnnotationSummaryAsyncUpdateService;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AnnotationSummaryAsyUpdateServiceImpl implements AnnotationSummaryAsyncUpdateService {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskDocRepository annotationTaskDocRepository;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository;

  public AnnotationSummaryAsyUpdateServiceImpl(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationTaskDocRepository annotationTaskDocRepository,
      final AnnotationCombineRepository annotationCombineRepository,
      final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskDocRepository = annotationTaskDocRepository;
    this.annotationCombineRepository = annotationCombineRepository;
    this.annotationStaffEvaluateRepository = annotationStaffEvaluateRepository;
  }

  /** 0 0/5 * * * ? 秒 分 时 天(月) 月 天(星期) 天(月),天(星期)互斥，任意一个为? 当前的corn表达式的意思时每隔5分钟触发一次 */
  @Override
  @Scheduled(cron = "0 0/5 * * * ?")
  @Transactional
  public List<AnnotationTask> asyncUpdateAnnotationOverview() {
    try {
      final List<AnnotationTask> annotationTasks = annotationTaskRepository.findAll();
      log.info("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
      IntStream.range(0, annotationTasks.size())
          .forEach(
              i -> {
                final AnnotationTask annotationTask = annotationTasks.get(i);
                final List<AnnotationTaskBlock> annotationTaskBlocks =
                    getBlocks(annotationTask.getId());
                annotationTask.setTotalBranchNum(
                    getOverviewBranchNum(
                        annotationTaskBlocks, AnnotationEvaluateStateEnum.total_branch));

                annotationTask.setAnnotatedBranchNum(
                    getOverviewBranchNum(
                        annotationTaskBlocks, AnnotationEvaluateStateEnum.annotated_branch));

                annotationTask.setRestBranchNum(
                    getOverviewBranchNum(
                        annotationTaskBlocks, AnnotationEvaluateStateEnum.rest_branch));

                annotationTask.setTotalWordNum(
                    getOverviewWordNum(
                        annotationTaskBlocks, AnnotationEvaluateStateEnum.total_word));

                annotationTask.setAnnotatedWordNum(
                    getOverviewWordNum(
                        annotationTaskBlocks, AnnotationEvaluateStateEnum.annotated_word));

                annotationTask.setRestWordNum(
                    getOverviewWordNum(
                        annotationTaskBlocks, AnnotationEvaluateStateEnum.rest_word));

                annotationTask.setInConformity(0);
              });
      return annotationTaskRepository.saveAll(annotationTasks);
    } catch (Exception ex) {
      log.info("定时任务，更新task表，发生异常:{}", ex.getMessage());
    }
    return null;
  }

  @Override
  @Scheduled(cron = "0 0/5 * * * ?")
  @Transactional
  public List<AnnotationTask> asyncUpdateAnnotationEvaluate() {
    final List<AnnotationTask> annotationTasks = annotationTaskRepository.findAll();
    log.info("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    IntStream.range(0, annotationTasks.size())
        .forEach(
            i -> {
              final AnnotationTask annotationTask = annotationTasks.get(i);
              final List<AnnotationCombine> annotationCombines =
                  annotationCombineRepository.findAllByBlockIdIn(
                      getBlocksId(annotationTask.getId()));
              final List<AnnotationStaffEvaluate> annotationStaffEvaluates =
                  getAnnotationStaffEvaluates(annotationCombines, annotationTask);
              IntStream.range(0, annotationStaffEvaluates.size())
                  .forEach(
                      k -> {
                        final AnnotationStaffEvaluate annotationStaffEvaluate =
                            annotationStaffEvaluates.get(k);
                        AnnotationStaffEvaluate updateAnnotationStaffEvaluate =
                            annotationStaffEvaluateRepository
                                .findByTaskIdEqualsAndAssigneeEqualsAndWorkDayEquals(
                                    annotationStaffEvaluate.getTaskId(),
                                    annotationStaffEvaluate.getAssignee(),
                                    annotationStaffEvaluate.getWorkDay());
                        if (updateAnnotationStaffEvaluate == null) {
                          final AnnotationStaffEvaluate current =
                              annotationStaffEvaluateRepository.save(annotationStaffEvaluate);
                          log.info("新增annotationStaffEvaluate，id为：{}", current.getId());
                        } else {
                          BeanUtils.copyProperties(
                              annotationStaffEvaluate,
                              updateAnnotationStaffEvaluate,
                              "id",
                              "taskId",
                              "taskName",
                              "workDay",
                              "assignee");
                          annotationStaffEvaluateRepository.save(updateAnnotationStaffEvaluate);
                          log.info(
                              "更新annotationStaffEvaluate，id为：{}",
                              updateAnnotationStaffEvaluate.getId());
                        }
                      });
            });
    return null;
  }

  private List<AnnotationStaffEvaluate> getAnnotationStaffEvaluates(
      final List<AnnotationCombine> annotationCombines, final AnnotationTask task) {
    annotationCombines
        .stream()
        .forEach(
            annotationCombine -> {
              if (annotationCombine
                  .getState()
                  .equals(AnnotationCombineStateEnum.preExamine.name())) {
                log.info("current：{}", annotationCombine);
              }
            });
    Map<Pair<Integer, java.sql.Date>, List<AnnotationCombine>> map =
        annotationCombines
            .stream()
            .collect(
                Collectors.groupingBy(
                    annotationCombine ->
                        Pair.of(
                            annotationCombine.getAssignee(),
                            annotationCombine.getCommitTimestamp())));
    return map.keySet()
        .stream()
        .map(
            key ->
                new AnnotationStaffEvaluate(
                    task.getId(),
                    task.getName(),
                    key.getLeft(),
                    key.getRight(),
                    getBranchNum(annotationCombines, AnnotationEvaluateStateEnum.total_branch),
                    getWordNum(annotationCombines, AnnotationEvaluateStateEnum.total_word),
                    getBranchNum(map.get(key), AnnotationEvaluateStateEnum.annotated_branch),
                    getWordNum(map.get(key), AnnotationEvaluateStateEnum.annotated_word),
                    getBranchNum(annotationCombines, AnnotationEvaluateStateEnum.rest_branch),
                    getWordNum(annotationCombines, AnnotationEvaluateStateEnum.rest_word),
                    0,
                    getBranchNum(map.get(key), AnnotationEvaluateStateEnum.abandon_branch),
                    getWordNum(map.get(key), AnnotationEvaluateStateEnum.abandon_word)))
        .collect(Collectors.toList());
  }

  private List<AnnotationTaskBlock> getBlocks(final int taskId) {
    return annotationTaskDocRepository
        .findAllByTask(new AnnotationTask(taskId))
        .stream()
        .flatMap(annotationTaskDoc -> annotationTaskDoc.getBlocks().stream())
        .map(annotationTaskDocBlock -> annotationTaskDocBlock.getBlock())
        .collect(Collectors.toList());
  }

  private List<Integer> getBlocksId(final int taskId) {
    return annotationTaskDocRepository
        .findAllByTask(new AnnotationTask(taskId))
        .stream()
        .flatMap(annotationTaskDoc -> annotationTaskDoc.getBlocks().stream())
        .map(annotationTaskDocBlock -> annotationTaskDocBlock.getId().getBlockId())
        .collect(Collectors.toList());
  }

  private int getOverviewWordNum(
      final List<AnnotationTaskBlock> annotationTaskBlocks,
      final AnnotationEvaluateStateEnum annotationEvaluateStateEnum) {
    List<String> annotationTaskStateList = Collections.emptyList();
    switch (annotationEvaluateStateEnum) {
      case total_word:
        annotationTaskStateList =
            Arrays.asList(
                AnnotationTaskState.CREATED.name(),
                AnnotationTaskState.DOING.name(),
                AnnotationTaskState.FINISHED.name(),
                AnnotationTaskState.ANNOTATED.name());
        break;
      case rest_word:
        annotationTaskStateList =
            Arrays.asList(AnnotationTaskState.CREATED.name(), AnnotationTaskState.DOING.name());
        break;
      case annotated_word:
        annotationTaskStateList =
            Arrays.asList(
                AnnotationTaskState.FINISHED.name(), AnnotationTaskState.ANNOTATED.name());
        break;
    }
    if (annotationTaskStateList.size() > 0) {
      final List<String> finalAnnotationTaskStateList = annotationTaskStateList;
      return annotationTaskBlocks
          .stream()
          .filter(
              annotationTaskBlock ->
                  finalAnnotationTaskStateList.contains(annotationTaskBlock.getState().name()))
          .mapToInt(value -> value.getText().length())
          .sum();
    }
    return 0;
  }

  private int getOverviewBranchNum(
      final List<AnnotationTaskBlock> annotationTaskBlocks,
      final AnnotationEvaluateStateEnum annotationEvaluateStateEnum) {
    List<String> annotationTaskStateList = Collections.emptyList();
    switch (annotationEvaluateStateEnum) {
      case total_branch:
        annotationTaskStateList =
            Arrays.asList(
                AnnotationTaskState.CREATED.name(),
                AnnotationTaskState.DOING.name(),
                AnnotationTaskState.FINISHED.name(),
                AnnotationTaskState.ANNOTATED.name());
        break;
      case annotated_branch:
        annotationTaskStateList =
            Arrays.asList(
                AnnotationTaskState.FINISHED.name(), AnnotationTaskState.ANNOTATED.name());
        break;
      case rest_branch:
        annotationTaskStateList =
            Arrays.asList(AnnotationTaskState.CREATED.name(), AnnotationTaskState.DOING.name());
        break;
    }
    if (annotationTaskStateList.size() > 0) {
      final List<String> finalAnnotationTaskStateList = annotationTaskStateList;
      return annotationTaskBlocks
          .stream()
          .filter(
              annotationTaskBlock ->
                  finalAnnotationTaskStateList.contains(annotationTaskBlock.getState().name()))
          .collect(Collectors.toList())
          .size();
    }
    return 0;
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
      final AnnotationEvaluateStateEnum annotationEvaluateStateEnum) {
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
