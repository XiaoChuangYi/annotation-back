package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationStaffEvaluateRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationEvaluateStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.service.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnnotationSummaryAsyUpdateServiceImpl {
  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository;

  public AnnotationSummaryAsyUpdateServiceImpl(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationCombineRepository annotationCombineRepository,
      final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationCombineRepository = annotationCombineRepository;
    this.annotationStaffEvaluateRepository = annotationStaffEvaluateRepository;
  }

  /** 0 0/5 * * * ? 秒 分 时 天(月) 月 天(星期) 天(月),天(星期)互斥，任意一个为? 当前的corn表达式的意思时每隔5分钟触发一次 */
  @Scheduled(cron = "0 0/10 * * * ?")
  @Transactional
  public void asyncUpdateAnnotationOverview() {
    log.info("asyncUpdateAnnotationOverview, time: {}", new Date());

    try {
      final List<AnnotationTask> annotationTasks =
          getTasks().stream().map(this::refreshTaskNumbers).collect(Collectors.toList());
      annotationTaskRepository.saveAll(annotationTasks);
    } catch (Exception ex) {
      log.info("定时任务，更新task表，发生异常:{}", ex.getMessage());
    }
  }

  @NotNull
  private AnnotationTask refreshTaskNumbers(final AnnotationTask annotationTask) {
    final List<AnnotationTaskBlock> annotationTaskBlocks = getBlocks(annotationTask.getId());

    annotationTask.setTotalBranchNum(
        getOverviewBranchNum(annotationTaskBlocks, AnnotationEvaluateStateEnum.TOTAL));

    annotationTask.setAnnotatedBranchNum(
        getOverviewBranchNum(annotationTaskBlocks, AnnotationEvaluateStateEnum.ANNOTATED));

    annotationTask.setRestBranchNum(
        getOverviewBranchNum(annotationTaskBlocks, AnnotationEvaluateStateEnum.REST));

    annotationTask.setTotalWordNum(
        getOverviewWordNum(annotationTaskBlocks, AnnotationEvaluateStateEnum.TOTAL));

    annotationTask.setAnnotatedWordNum(
        getOverviewWordNum(annotationTaskBlocks, AnnotationEvaluateStateEnum.ANNOTATED));

    annotationTask.setRestWordNum(
        getOverviewWordNum(annotationTaskBlocks, AnnotationEvaluateStateEnum.REST));

    annotationTask.setInConformity(0);
    return annotationTask;
  }

  @Scheduled(cron = "0 0/10 * * * ?")
  @Transactional
  public void asyncUpdateAnnotationEvaluate() {
    log.info("asyncUpdateAnnotationEvaluate time: {}", new Date());
    final List<AnnotationTask> annotationTasks = getTasks();
    annotationTasks.forEach(
        annotationTask -> {
          final List<AnnotationTaskBlock> blocks = getBlocks(annotationTask.getId());
          final List<AnnotationCombine> annotationCombines =
              annotationCombineRepository.findAllByBlockIdIn(
                  blocks.stream().map(BaseEntity::getId).collect(Collectors.toList()));
          final List<AnnotationStaffEvaluate> annotationStaffEvaluates =
              getAnnotationStaffEvaluates(annotationCombines, annotationTask);
          annotationStaffEvaluates.forEach(
              annotationStaffEvaluate -> {
                final AnnotationStaffEvaluate updateAnnotationStaffEvaluate =
                    annotationStaffEvaluateRepository.findByTaskIdAndAssigneeAndWorkDay(
                        annotationStaffEvaluate.getTaskId(),
                        annotationStaffEvaluate.getAssignee(),
                        annotationStaffEvaluate.getWorkDay());

                if (updateAnnotationStaffEvaluate == null) {
                  final AnnotationStaffEvaluate current =
                      annotationStaffEvaluateRepository.save(annotationStaffEvaluate);
                  log.info("新增annotationStaffEvaluate，id为：{}", current.getId());
                } else {
                  BeanUtils.copyProperties(
                      annotationStaffEvaluate, updateAnnotationStaffEvaluate, "id");
                  annotationStaffEvaluateRepository.save(updateAnnotationStaffEvaluate);
                  log.info(
                      "更新annotationStaffEvaluate，id为：{}", updateAnnotationStaffEvaluate.getId());
                }
              });
        });
  }

  private List<AnnotationStaffEvaluate> getAnnotationStaffEvaluates(
      final List<AnnotationCombine> annotationCombines, final AnnotationTask task) {
    final Map<Pair<Long, java.sql.Date>, List<AnnotationCombine>> map =
        annotationCombines
            .stream()
            .collect(
                Collectors.groupingBy(
                    annotationCombine ->
                        Pair.of(
                            annotationCombine.getAssignee(),
                            annotationCombine.getCommitTimestamp() == null
                                ? new java.sql.Date(new Date().getTime())
                                : new java.sql.Date(
                                    annotationCombine.getCommitTimestamp().getTime()))));

    final int totalBranchNum = getBranchNum(annotationCombines, AnnotationEvaluateStateEnum.TOTAL);
    final int totalWordNum = getWordNum(annotationCombines, AnnotationEvaluateStateEnum.TOTAL);
    final int restBranchNum = getBranchNum(annotationCombines, AnnotationEvaluateStateEnum.REST);
    final int restWordNum = getWordNum(annotationCombines, AnnotationEvaluateStateEnum.REST);

    return map.entrySet()
        .stream()
        .map(
            entry ->
                new AnnotationStaffEvaluate(
                    task.getId(),
                    task.getName(),
                    entry.getKey().getRight(),
                    entry.getKey().getLeft(),
                    totalBranchNum,
                    totalWordNum,
                    getBranchNum(entry.getValue(), AnnotationEvaluateStateEnum.ANNOTATED),
                    getWordNum(entry.getValue(), AnnotationEvaluateStateEnum.ANNOTATED),
                    restBranchNum,
                    restWordNum,
                    0,
                    getBranchNum(entry.getValue(), AnnotationEvaluateStateEnum.ABANDON),
                    getWordNum(entry.getValue(), AnnotationEvaluateStateEnum.ABANDON)))
        .collect(Collectors.toList());
  }

  private List<AnnotationTask> getTasks() {
    return annotationTaskRepository.findByStateNotIn(
        Arrays.asList(AnnotationTaskState.CREATED, AnnotationTaskState.FINISHED));
  }

  private List<AnnotationTaskBlock> getBlocks(final long taskId) {
    return annotationTaskBlockRepository.findByTaskDocs_TaskDoc_Task_IdEquals(taskId);
  }

  private int getOverviewWordNum(
      final List<AnnotationTaskBlock> annotationTaskBlocks,
      final AnnotationEvaluateStateEnum state) {
    return annotationTaskBlocks
        .stream()
        .filter(
            annotationTaskBlock -> state.getBlockStates().contains(annotationTaskBlock.getState()))
        .mapToInt(value -> value.getText().length())
        .sum();
  }

  private int getOverviewBranchNum(
      final List<AnnotationTaskBlock> annotationTaskBlocks,
      final AnnotationEvaluateStateEnum state) {
    return annotationTaskBlocks
        .stream()
        .filter(
            annotationTaskBlock -> state.getBlockStates().contains(annotationTaskBlock.getState()))
        .collect(Collectors.toList())
        .size();
  }

  private int getBranchNum(
      final List<AnnotationCombine> annotationCombines, final AnnotationEvaluateStateEnum state) {
    return annotationCombines
        .stream()
        .filter(
            annotationCombine ->
                state.getAnnotationStates().contains(annotationCombine.getStateEnum()))
        .collect(Collectors.toList())
        .size();
  }

  private int getWordNum(
      final List<AnnotationCombine> annotationCombines, final AnnotationEvaluateStateEnum state) {
    return annotationCombines
        .stream()
        .filter(
            annotationCombine ->
                state.getAnnotationStates().contains(annotationCombine.getStateEnum()))
        .mapToInt(value -> value.getTerm().length())
        .sum();
  }
}
