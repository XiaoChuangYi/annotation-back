package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationStaffEvaluateRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationEvaluateStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.core.definition.Entity;
import cn.malgo.service.entity.BaseEntity;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AnnotationSummaryAsyUpdateServiceImpl {
  private static final String CRON_STR = "0 0/5 * * * ?";
  //  private static final String CRON_STR = "0/30 * * * * ?";

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository;
  private final AnnotationFactory annotationFactory;

  public AnnotationSummaryAsyUpdateServiceImpl(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationCombineRepository annotationCombineRepository,
      final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository,
      final AnnotationFactory annotationFactory) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationCombineRepository = annotationCombineRepository;
    this.annotationStaffEvaluateRepository = annotationStaffEvaluateRepository;
    this.annotationFactory = annotationFactory;
  }

  /** 0 0/5 * * * ? 秒 分 时 天(月) 月 天(星期) 天(月),天(星期)互斥，任意一个为? 当前的corn表达式的意思时每隔5分钟触发一次 */
  @Scheduled(cron = AnnotationSummaryAsyUpdateServiceImpl.CRON_STR)
  @Transactional
  public void asyncUpdateAnnotationOverview() {
    log.info("asyncUpdateAnnotationOverview, start: {}", new Date());

    try {
      final List<AnnotationTask> annotationTasks =
          getTasks().stream().map(this::refreshTaskNumbers).collect(Collectors.toList());
      annotationTaskRepository.saveAll(annotationTasks);
    } catch (Exception ex) {
      log.info("定时任务，更新task表，发生异常:{}", ex.getMessage());
    }

    log.info("asyncUpdateAnnotationOverview, end: {}", new Date());
  }

  private Map<String, EntitySummary> getEntityMap(final Annotation annotation) {
    return annotation
        .getDocument()
        .getEntities()
        .stream()
        .collect(Collectors.toMap(Entity::getTag, EntitySummary::new));
  }

  private Set<RelationEntitySummary> getRelations(
      final Annotation annotation, final Map<String, EntitySummary> entityMap) {
    return annotation
        .getDocument()
        .getRelationEntities()
        .stream()
        .map(
            entity ->
                new RelationEntitySummary(
                    entityMap.get(entity.getSourceTag()),
                    entityMap.get(entity.getTargetTag()),
                    entity.getType()))
        .collect(Collectors.toSet());
  }

  private <T> Pair<Double, Double> getInConformity(
      final Collection<T> annotation, final Collection<T> block) {
    final int sameEntityCount = CollectionUtils.intersection(annotation, block).size();
    return Pair.of(
        annotation.size() == 0 ? 1 : sameEntityCount / (double) annotation.size(),
        block.size() == 0 ? 1 : sameEntityCount / (double) block.size());
  }

  private Pair<Double, Double> getInConformity(
      final Annotation annotation, final Annotation block) {
    if (annotation == null || block == null) {
      log.warn("calculate inconformity get null annotation: {} or block: {}", annotation, block);
      return Pair.of(1.0, 1.0);
    }

    final Map<String, EntitySummary> annotationEntities = getEntityMap(annotation);
    final Map<String, EntitySummary> blockEntities = getEntityMap(block);

    final Set<RelationEntitySummary> annotationRelations =
        getRelations(annotation, annotationEntities);
    final Set<RelationEntitySummary> blockRelations = getRelations(block, blockEntities);

    final Pair<Double, Double> entityResult =
        getInConformity(annotationEntities.values(), blockEntities.values());
    final Pair<Double, Double> relationResult =
        getInConformity(annotationRelations, blockRelations);

    return Pair.of(
        (entityResult.getLeft() + relationResult.getLeft()) / 2,
        (entityResult.getRight() + relationResult.getRight()) / 2);
  }

  private boolean isAnnotated(final AnnotationCombine annotationCombine) {
    if (annotationCombine.getAssignee() <= 1) {
      return false;
    }

    final AnnotationCombineStateEnum state = annotationCombine.getStateEnum();
    return state == AnnotationCombineStateEnum.errorPass
        || state == AnnotationCombineStateEnum.examinePass
        || state == AnnotationCombineStateEnum.innerAnnotation;
  }

  @NotNull
  private AnnotationTask refreshTaskNumbers(final AnnotationTask annotationTask) {
    final Set<AnnotationTaskBlock> blocks = getBlocks(annotationTask.getId());
    final Map<Long, Annotation> blockMap = getBlockMap(blocks);

    annotationTask.setTotalBranchNum(
        getOverviewBranchNum(blocks, AnnotationEvaluateStateEnum.TOTAL));

    annotationTask.setAnnotatedBranchNum(
        getOverviewBranchNum(blocks, AnnotationEvaluateStateEnum.ANNOTATED));

    annotationTask.setRestBranchNum(getOverviewBranchNum(blocks, AnnotationEvaluateStateEnum.REST));

    annotationTask.setTotalWordNum(getOverviewWordNum(blocks, AnnotationEvaluateStateEnum.TOTAL));

    annotationTask.setAnnotatedWordNum(
        getOverviewWordNum(blocks, AnnotationEvaluateStateEnum.ANNOTATED));

    annotationTask.setRestWordNum(getOverviewWordNum(blocks, AnnotationEvaluateStateEnum.REST));

    final Pair<Double, Double> result =
        getInConformity(
            annotationCombineRepository.findAllByBlockIdIn(blockMap.keySet()), blockMap);

    annotationTask.setPrecisionRate(result.getLeft());
    annotationTask.setRecallRate(result.getRight());

    return annotationTask;
  }

  private Pair<Double, Double> getInConformity(
      final List<AnnotationCombine> annotationCombines, final Map<Long, Annotation> blockMap) {
    final List<Pair<Double, Double>> values =
        annotationCombines
            .stream()
            .filter(this::isAnnotated)
            .map(
                annotation ->
                    getInConformity(
                        this.annotationFactory.create(annotation),
                        blockMap.get(annotation.getBlockId())))
            .collect(Collectors.toList());

    return Pair.of(
        values.stream().mapToDouble(Pair::getLeft).average().orElse(1),
        values.stream().mapToDouble(Pair::getRight).average().orElse(1));
  }

  @Scheduled(cron = AnnotationSummaryAsyUpdateServiceImpl.CRON_STR)
  @Transactional
  public void asyncUpdateAnnotationEvaluate() {
    log.info("asyncUpdateAnnotationEvaluate, start: {}", new Date());

    final List<AnnotationTask> annotationTasks = getTasks();
    annotationTasks.forEach(
        annotationTask -> {
          final Set<AnnotationTaskBlock> blocks = getBlocks(annotationTask.getId());
          final Map<Long, Annotation> blockMap = getBlockMap(blocks);
          final List<AnnotationCombine> annotationCombines =
              annotationCombineRepository.findAllByBlockIdIn(
                  blocks.stream().map(BaseEntity::getId).collect(Collectors.toSet()));
          final List<AnnotationStaffEvaluate> annotationStaffEvaluates =
              getAnnotationStaffEvaluates(blockMap, annotationCombines, annotationTask);
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

    log.info("asyncUpdateAnnotationEvaluate, end: {}", new Date());
  }

  private Map<Long, Annotation> getBlockMap(Set<AnnotationTaskBlock> blocks) {
    return blocks
        .stream()
        .filter(
            block ->
                block.getState() == AnnotationTaskState.ANNOTATED
                    || block.getState() == AnnotationTaskState.FINISHED)
        .collect(Collectors.toMap(AnnotationTaskBlock::getId, this.annotationFactory::create));
  }

  private String getDate(Date timestamp) {
    final Calendar calendar = Calendar.getInstance();
    if (timestamp != null) {
      calendar.setTime(timestamp);
    }

    return calendar.get(Calendar.YEAR)
        + "-"
        + (calendar.get(Calendar.MONTH) + 1)
        + "-"
        + calendar.get(Calendar.DATE);
  }

  private List<AnnotationStaffEvaluate> getAnnotationStaffEvaluates(
      final Map<Long, Annotation> blockMap,
      final List<AnnotationCombine> annotationCombines,
      final AnnotationTask task) {
    final Map<Long, List<AnnotationCombine>> assigneeMap =
        annotationCombines.stream().collect(Collectors.groupingBy(AnnotationCombine::getAssignee));

    return assigneeMap
        .entrySet()
        .stream()
        .flatMap(
            entry -> {
              final int totalBranchNum =
                  getBranchNum(entry.getValue(), AnnotationEvaluateStateEnum.TOTAL);
              final int totalWordNum =
                  getWordNum(entry.getValue(), AnnotationEvaluateStateEnum.TOTAL);
              final int restBranchNum =
                  getBranchNum(entry.getValue(), AnnotationEvaluateStateEnum.REST);
              final int restWordNum =
                  getWordNum(entry.getValue(), AnnotationEvaluateStateEnum.REST);

              return entry
                  .getValue()
                  .stream()
                  .collect(Collectors.groupingBy(ann -> getDate(ann.getCommitTimestamp())))
                  .entrySet()
                  .stream()
                  .map(
                      dateEntry -> {
                        final Pair<Double, Double> result =
                            getInConformity(dateEntry.getValue(), blockMap);

                        return new AnnotationStaffEvaluate(
                            task.getId(),
                            task.getName(),
                            java.sql.Date.valueOf(dateEntry.getKey()),
                            entry.getKey(),
                            totalBranchNum,
                            totalWordNum,
                            getBranchNum(
                                dateEntry.getValue(), AnnotationEvaluateStateEnum.ANNOTATED),
                            getWordNum(dateEntry.getValue(), AnnotationEvaluateStateEnum.ANNOTATED),
                            restBranchNum,
                            restWordNum,
                            getBranchNum(dateEntry.getValue(), AnnotationEvaluateStateEnum.ABANDON),
                            getWordNum(dateEntry.getValue(), AnnotationEvaluateStateEnum.ABANDON),
                            result.getLeft(),
                            result.getRight());
                      });
            })
        .collect(Collectors.toList());
  }

  private List<AnnotationTask> getTasks() {
    return annotationTaskRepository.findByStateNotIn(
        Arrays.asList(AnnotationTaskState.CREATED, AnnotationTaskState.FINISHED));
  }

  private Set<AnnotationTaskBlock> getBlocks(final long taskId) {
    return annotationTaskBlockRepository.findByTaskDocs_TaskDoc_Task_IdEquals(taskId);
  }

  private int getOverviewWordNum(
      final Collection<AnnotationTaskBlock> annotationTaskBlocks,
      final AnnotationEvaluateStateEnum state) {
    return annotationTaskBlocks
        .stream()
        .filter(
            annotationTaskBlock -> state.getBlockStates().contains(annotationTaskBlock.getState()))
        .mapToInt(value -> value.getText().length())
        .sum();
  }

  private int getOverviewBranchNum(
      final Collection<AnnotationTaskBlock> annotationTaskBlocks,
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

  @Value
  static class EntitySummary {
    private int start;
    private int end;
    private String type;

    public EntitySummary(final Entity entity) {
      this.start = entity.getStart();
      this.end = entity.getEnd();
      this.type = entity.getType();
    }
  }

  @Value
  static class RelationEntitySummary {
    private EntitySummary source;
    private EntitySummary target;
    private String type;
  }
}
