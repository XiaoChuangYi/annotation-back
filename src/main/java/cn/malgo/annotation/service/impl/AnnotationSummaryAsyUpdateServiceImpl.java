package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationStaffEvaluateRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.PersonalAnnotatedEstimatePriceRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
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
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnotationSummaryAsyUpdateServiceImpl implements AnnotationSummaryService {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationRepository annotationRepository;
  private final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository;
  private final AnnotationFactory annotationFactory;
  private final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository;

  public AnnotationSummaryAsyUpdateServiceImpl(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository,
      final AnnotationFactory annotationFactory,
      final AnnotationRepository annotationRepository,
      final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationRepository = annotationRepository;
    this.annotationStaffEvaluateRepository = annotationStaffEvaluateRepository;
    this.annotationFactory = annotationFactory;
    this.personalAnnotatedEstimatePriceRepository = personalAnnotatedEstimatePriceRepository;
  }

  @NotNull
  @Override
  public void updatePersonalAnnotatedWordNum(final AnnotationTask task) {
    annotationRepository
        .findAllByStateInAndBlockIdIn(
            Arrays.asList(AnnotationStateEnum.ANNOTATION_PROCESSING, AnnotationStateEnum.SUBMITTED),
            getBlockIds(
                task, Arrays.asList(AnnotationTaskState.DOING, AnnotationTaskState.ANNOTATED)))
        .parallelStream()
        .collect(Collectors.groupingBy(AnnotationNew::getAssignee))
        .entrySet()
        .parallelStream()
        .forEach(
            entry -> {
              final int annotatedTotalWordNum = getAnnotatedTermsLength(entry.getValue());
              PersonalAnnotatedTotalWordNumRecord current =
                  personalAnnotatedEstimatePriceRepository.findByTaskIdEqualsAndAssigneeIdEquals(
                      task.getId(), entry.getKey());
              if (current != null) {
                current.setAnnotatedTotalWordNum(annotatedTotalWordNum);
              } else {
                current =
                    new PersonalAnnotatedTotalWordNumRecord(
                        task.getId(), entry.getKey(), annotatedTotalWordNum);
              }
              personalAnnotatedEstimatePriceRepository.save(current);
            });
  }

  @Override
  public void updateAnnotationStateByExpirationTime(AnnotationTask task) {
    annotationRepository
        .findAllByStateInAndBlockIdIn(
            Arrays.asList(
                AnnotationStateEnum.ANNOTATION_PROCESSING, AnnotationStateEnum.PRE_ANNOTATION),
            getBlockIds(task, Arrays.asList(AnnotationTaskState.DOING)))
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

  private List<Long> getBlockIds(
      final AnnotationTask task, final List<AnnotationTaskState> annotationTaskStates) {
    return annotationTaskBlockRepository
        .findByStateInAndTaskBlocks_Task_Id(annotationTaskStates, task.getId())
        .stream()
        .map(annotationTaskBlock -> annotationTaskBlock.getId())
        .collect(Collectors.toList());
  }

  private int getAnnotatedTermsLength(List<AnnotationNew> annotationNews) {
    return annotationNews
        .parallelStream()
        .map(annotationNew -> this.annotationFactory.create(annotationNew))
        .flatMap(annotation -> annotation.getDocument().getEntities().stream())
        .mapToInt(value -> value.getTerm().length())
        .sum();
  }

  @NotNull
  @Override
  public AnnotationTask updateTaskSummary(final AnnotationTask annotationTask) {
    final AnnotationTask task = refreshTaskNumbers(annotationTask);

    final Set<AnnotationTaskBlock> blocks = getBlocks(annotationTask.getId());
    final Map<Long, Annotation> blockMap = getBlockMap(blocks);
    final List<AnnotationNew> annotationNews =
        annotationRepository.findAllByBlockIdIn(
            blocks.stream().map(BaseEntity::getId).collect(Collectors.toSet()));
    final List<AnnotationStaffEvaluate> annotationStaffEvaluates =
        getAnnotationStaffEvaluates(blockMap, annotationNews, annotationTask);
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
            BeanUtils.copyProperties(annotationStaffEvaluate, updateAnnotationStaffEvaluate, "id");
            annotationStaffEvaluateRepository.save(updateAnnotationStaffEvaluate);
            log.info("更新annotationStaffEvaluate，id为：{}", updateAnnotationStaffEvaluate.getId());
          }
        });
    return task;
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

  private boolean isAnnotated(final AnnotationNew annotationNew) {
    if (annotationNew.getAssignee() <= 1) {
      return false;
    }

    final AnnotationStateEnum state = annotationNew.getState();
    return state == AnnotationStateEnum.SUBMITTED;
  }

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
        getInConformity(annotationRepository.findAllByBlockIdIn(blockMap.keySet()), blockMap);

    annotationTask.setPrecisionRate(result.getLeft());
    annotationTask.setRecallRate(result.getRight());

    return annotationTask;
  }

  private Pair<Double, Double> getInConformity(
      final List<AnnotationNew> annotationNews, final Map<Long, Annotation> blockMap) {
    final List<Pair<Double, Double>> values =
        annotationNews
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
      final List<AnnotationNew> annotationNews,
      final AnnotationTask task) {
    final Map<Long, List<AnnotationNew>> assigneeMap =
        annotationNews.stream().collect(Collectors.groupingBy(AnnotationNew::getAssignee));

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
                            0,
                            0,
                            result.getLeft(),
                            result.getRight());
                      });
            })
        .collect(Collectors.toList());
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

  private int getBranchNum(
      final List<AnnotationNew> annotationNews, final AnnotationEvaluateStateEnum state) {
    return annotationNews
        .stream()
        .filter(annotationNew -> state.getAnnotationStates().contains(annotationNew.getState()))
        .collect(Collectors.toList())
        .size();
  }

  private int getWordNum(
      final List<AnnotationNew> annotationNews, final AnnotationEvaluateStateEnum state) {
    return annotationNews
        .stream()
        .filter(annotationNew -> state.getAnnotationStates().contains(annotationNew.getState()))
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
