package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.PersonalAnnotatedTotalWordNumRecordRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import cn.malgo.annotation.enums.AnnotationEvaluateStateEnum;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationSummaryService;
import cn.malgo.core.definition.Entity;
import cn.malgo.service.entity.BaseEntity;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnotationSummaryAsyUpdateServiceImpl implements AnnotationSummaryService {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationRepository annotationRepository;
  private final AnnotationFactory annotationFactory;
  private final AnnotationTaskRepository taskRepository;
  private final PersonalAnnotatedTotalWordNumRecordRepository
      personalAnnotatedEstimatePriceRepository;

  public AnnotationSummaryAsyUpdateServiceImpl(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationFactory annotationFactory,
      final AnnotationRepository annotationRepository,
      final AnnotationTaskRepository taskRepository,
      final PersonalAnnotatedTotalWordNumRecordRepository
          personalAnnotatedEstimatePriceRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationRepository = annotationRepository;
    this.annotationFactory = annotationFactory;
    this.taskRepository = taskRepository;
    this.personalAnnotatedEstimatePriceRepository = personalAnnotatedEstimatePriceRepository;
  }

  @Override
  public void updateTaskPersonalSummary(final AnnotationTask task) {
    if (task.getState() != AnnotationTaskState.FINISHED) {
      throw new IllegalStateException("invalid task state: " + task.getId());
    }

    final List<AnnotationNew> annotations = annotationRepository.findByTaskId(task.getId());
    if (annotations
        .stream()
        .anyMatch(
            annotation ->
                annotation.getState() != AnnotationStateEnum.CLEANED
                    && annotation.getState() != AnnotationStateEnum.PRE_CLEAN)) {
      throw new IllegalStateException("invalid annotation state: " + task.getId());
    }

    annotations
        .parallelStream()
        .collect(Collectors.groupingBy(AnnotationNew::getAssignee))
        .entrySet()
        .forEach(
            entry -> {
              PersonalAnnotatedTotalWordNumRecord current =
                  personalAnnotatedEstimatePriceRepository.findByTaskIdEqualsAndAssigneeIdEquals(
                      task.getId(), entry.getKey());
              final int annotatedTotalWordNum = getAnnotatedTermsLength(entry.getValue());

              if (current == null) {
                current = new PersonalAnnotatedTotalWordNumRecord();
                current.setTaskId(task.getId());
                current.setAssigneeId(entry.getKey());
              }

              current.setTotalWordNum(annotatedTotalWordNum);
              current.setAnnotatedTotalWordNum(annotatedTotalWordNum);

              if (checkStateIsCleaned(entry.getValue())) {
                current.setPrecisionRate(
                    entry
                        .getValue()
                        .stream()
                        .mapToDouble(AnnotationNew::getPrecisionRate)
                        .average()
                        .getAsDouble());
                current.setRecallRate(
                    entry
                        .getValue()
                        .stream()
                        .mapToDouble(AnnotationNew::getRecallRate)
                        .average()
                        .getAsDouble());
              }

              personalAnnotatedEstimatePriceRepository.save(current);
            });
  }

  private boolean checkStateIsCleaned(final List<AnnotationNew> annotationNews) {
    return annotationNews
        .parallelStream()
        .allMatch(annotationNew -> annotationNew.getState() == AnnotationStateEnum.CLEANED);
  }

  @Override
  public void updateAnnotationStateByExpirationTime(AnnotationTask task) {
    annotationRepository
        .findAllByStateInAndBlockIdIn(
            Arrays.asList(
                AnnotationStateEnum.ANNOTATION_PROCESSING, AnnotationStateEnum.PRE_ANNOTATION),
            getBlockIds(task, Collections.singletonList(AnnotationTaskState.DOING)))
        .parallelStream()
        .filter(
            annotationNew -> annotationNew.getExpirationTime().getTime() <= new Date().getTime())
        .forEach(
            annotationNew -> {
              annotationNew.setAssignee(0);
              annotationNew.setState(AnnotationStateEnum.UN_DISTRIBUTED);
              annotationRepository.save(annotationNew);
            });
  }

  @Override
  public void updateAnnotationPrecisionAndRecallRate(AnnotationNew annotation) {
    if (annotation.getState() != AnnotationStateEnum.PRE_CLEAN
        || annotation.getPrecisionRate() != null
        || annotation.getRecallRate() != null) {
      throw new IllegalStateException("invalid annotation state");
    }

    final Pair<Double, Double> pair =
        getInConformity(
            this.annotationFactory.create(annotation),
            this.annotationFactory.create(
                annotationTaskBlockRepository.getOne(annotation.getBlockId())));

    annotation.setPrecisionRate(pair.getLeft());
    annotation.setRecallRate(pair.getRight());
    annotation.setState(AnnotationStateEnum.CLEANED);
  }

  private List<Long> getBlockIds(
      final AnnotationTask task, final List<AnnotationTaskState> annotationTaskStates) {
    return annotationTaskBlockRepository
        .findByStateInAndTaskBlocks_Task_Id(annotationTaskStates, task.getId())
        .stream()
        .map(BaseEntity::getId)
        .collect(Collectors.toList());
  }

  private int getAnnotatedTermsLength(List<AnnotationNew> annotationNews) {
    return annotationNews.parallelStream().mapToInt(value -> value.getTerm().length()).sum();
  }

  @NotNull
  @Override
  public AnnotationTask updateTaskSummary(long id) {
    return taskRepository.save(refreshTaskNumbers(taskRepository.getOne(id)));
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

  private Pair<Double, Double> getInConformity(
      final Annotation annotation, final Annotation block) {
    if (annotation == null || block == null) {
      log.warn("calculate inconformity get null annotation: {} or block: {}", annotation, block);
      return Pair.of(null, null);
    }

    final Map<String, EntitySummary> annotationEntities = getEntityMap(annotation);
    final Map<String, EntitySummary> blockEntities = getEntityMap(block);

    final Set<RelationEntitySummary> annotationRelations =
        getRelations(annotation, annotationEntities);
    final Set<RelationEntitySummary> blockRelations = getRelations(block, blockEntities);

    final int sameEntityCount =
        CollectionUtils.intersection(annotationEntities.values(), blockEntities.values()).size();
    final int sameRelationCount =
        CollectionUtils.intersection(annotationRelations, blockRelations).size();
    if ((sameEntityCount + sameRelationCount) == 0) {
      return Pair.of(0d, 0d);
    }
    return Pair.of(
        (sameEntityCount + sameRelationCount)
            / (double) (annotationEntities.values().size() + annotationRelations.size()),
        (sameEntityCount + sameRelationCount)
            / (double) (blockEntities.values().size() + blockRelations.size()));
  }

  private AnnotationTask refreshTaskNumbers(final AnnotationTask annotationTask) {
    final Set<AnnotationTaskBlock> blocks = getBlocks(annotationTask.getId());
    annotationTask.setTotalBranchNum(
        getOverviewBranchNum(blocks, AnnotationEvaluateStateEnum.TOTAL));

    annotationTask.setAnnotatedBranchNum(
        getOverviewBranchNum(blocks, AnnotationEvaluateStateEnum.ANNOTATED));

    annotationTask.setRestBranchNum(getOverviewBranchNum(blocks, AnnotationEvaluateStateEnum.REST));

    annotationTask.setTotalWordNum(getOverviewWordNum(blocks, AnnotationEvaluateStateEnum.TOTAL));

    annotationTask.setAnnotatedWordNum(
        getOverviewWordNum(blocks, AnnotationEvaluateStateEnum.ANNOTATED));

    annotationTask.setRestWordNum(getOverviewWordNum(blocks, AnnotationEvaluateStateEnum.REST));

    if (annotationTask.getState() == AnnotationTaskState.FINISHED) {
      final List<AnnotationNew> annotations =
          annotationRepository.findByTaskId(annotationTask.getId());

      if (annotations
          .stream()
          .anyMatch(
              annotation ->
                  annotation.getState() != AnnotationStateEnum.CLEANED
                      && annotation.getState() != AnnotationStateEnum.PRE_CLEAN)) {
        throw new IllegalStateException("invalid annotation state: " + annotationTask.getId());
      }

      if (annotations.size() > 0 && checkStateIsCleaned(annotations)) {
        annotationTask.setPrecisionRate(
            annotations
                .stream()
                .mapToDouble(AnnotationNew::getPrecisionRate)
                .average()
                .getAsDouble());

        annotationTask.setRecallRate(
            annotations.stream().mapToDouble(AnnotationNew::getRecallRate).average().getAsDouble());
      }
    }

    return annotationTask;
  }

  private Set<AnnotationTaskBlock> getBlocks(final long taskId) {
    return annotationTaskBlockRepository.findByTaskBlocks_Task_IdEquals(taskId);
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
