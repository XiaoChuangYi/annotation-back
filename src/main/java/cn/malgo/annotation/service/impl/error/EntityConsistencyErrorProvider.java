package cn.malgo.annotation.service.impl.error;

import static cn.malgo.annotation.constants.AnnotationErrorConsts.IGNORE_WORDS;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.AnnotationWithPosition;
import cn.malgo.annotation.dto.error.FixAnnotationEntity;
import cn.malgo.annotation.dto.error.FixAnnotationErrorData;
import cn.malgo.annotation.dto.error.FixAnnotationRelationEntity;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.DocumentUtils;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.RelationEntity;
import cn.malgo.core.definition.brat.BratPosition;
import cn.malgo.service.exception.InternalServerException;
import cn.malgo.service.exception.InvalidInputException;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EntityConsistencyErrorProvider extends BaseErrorProvider {
  private final int batchSize;
  private Set<Long> cachedAnnotationIds = new HashSet<>(100000);
  private Map<String, Set<Long>> cachedTerms = new HashMap<>(100000);

  public EntityConsistencyErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository,
      @Value("${malgo.annotation.fix-log-batch-size}") final int batchSize) {
    super(annotationFixLogRepository);

    this.batchSize = batchSize;
  }

  @Override
  public AnnotationErrorEnum getErrorEnum() {
    return AnnotationErrorEnum.ENTITY_CONSISTENCY;
  }

  @Override
  public List<AlgorithmAnnotationWordError> find(final List<Annotation> annotations) {
    final Comparator<String> comparator = (lhs, rhs) -> rhs.length() - lhs.length();

    final List<String> terms =
        annotations
            .parallelStream()
            .flatMap(
                annotation -> annotation.getDocument().getEntities().stream().map(Entity::getTerm))
            .filter(term -> StringUtils.isNotBlank(term) && !IGNORE_WORDS.contains(term))
            .collect(Collectors.toSet())
            .parallelStream()
            .sorted(comparator)
            .collect(Collectors.toList());

    log.info("get all terms size: {}", terms.size());

    cachedTerms.putAll(
        terms
            .parallelStream()
            .filter(term -> !cachedTerms.containsKey(term))
            .collect(
                Collectors.toMap(term -> term, term -> getTermAnnotations(term, annotations))));

    final List<Annotation> newAnnotations =
        annotations
            .parallelStream()
            .filter(ann -> !cachedAnnotationIds.contains(ann.getId()))
            .collect(Collectors.toList());

    cachedAnnotationIds.addAll(
        newAnnotations.parallelStream().map(Annotation::getId).collect(Collectors.toSet()));

    cachedTerms
        .entrySet()
        .parallelStream()
        .forEach(
            entry -> entry.getValue().addAll(getTermAnnotations(entry.getKey(), newAnnotations)));

    log.info("cached terms size: {}", cachedTerms.size());

    final Map<Long, Annotation> annotationMap =
        annotations.parallelStream().collect(Collectors.toMap(Annotation::getId, ann -> ann));

    final List<EntityListWithPosition> entityListWithPosition =
        cachedTerms
            .entrySet()
            .parallelStream()
            .map(
                entry ->
                    entry
                        .getValue()
                        .parallelStream()
                        .flatMap(id -> this.getTermEntities(annotationMap.get(id), entry.getKey()))
                        .collect(Collectors.toList()))
            .filter(entityLists -> !isSingleEntitySize(entityLists))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    log.info("get all possible entities size: {}", entityListWithPosition.size());

    return postProcess(
        Lists.partition(entityListWithPosition, this.batchSize)
            .parallelStream()
            // 过滤已经被处理过的错误
            .flatMap(this::filterErrors)
            .collect(Collectors.groupingBy(EntityListWithPosition::getTerm))
            .values()
            .parallelStream()
            .filter(entityLists -> !isSingleEntitySize(entityLists))
            .flatMap(Collection::stream)
            .map(
                entityList ->
                    new WordErrorWithPosition(
                        entityList.getTerm(),
                        entityList.getEntitySize() == 1 ? "单一实体" : "子图",
                        entityList.getPosition(),
                        entityList.getAnnotation(),
                        null))
            .collect(Collectors.toList()),
        0);
  }

  @Override
  public List<Entity> fix(
      final Annotation annotation,
      final int start,
      final int end,
      final FixAnnotationErrorData data) {
    log.info("fixing {}, position: ({}, {}), data: {}", annotation.getId(), start, end, data);

    final int activeEntity = data.getActiveEntity();
    final List<FixAnnotationEntity> entities = data.getEntities();
    final List<FixAnnotationRelationEntity> relations =
        data.getRelations() != null ? data.getRelations() : Collections.emptyList();

    if (activeEntity < 0 || activeEntity >= entities.size()) {
      throw new InvalidInputException(
          "invalid-active-entity", activeEntity + "不是一个合法的index位置，请检查表格");
    }

    final AnnotationDocument document = annotation.getDocument();
    final String text = document.getText();
    final String subString = text.substring(start, end);

    if (entities.stream().anyMatch(entity -> !StringUtils.contains(subString, entity.getTerm()))) {
      throw new InvalidInputException(
          "invalid-fix-entities", "存在不包含在\"" + text + "\"中的entity，请检查表格");
    }

    if (relations
        .stream()
        .anyMatch(
            entity ->
                entity.getSource() < 0
                    || entity.getSource() >= entities.size()
                    || entity.getTarget() < 0
                    || entity.getTarget() >= entities.size())) {
      throw new InvalidInputException("invalid-relations", "relations中包含非法index");
    }

    final int oldEntitySize =
        document
            .getEntities()
            .stream()
            .mapToInt(entity -> Integer.parseInt(entity.getTag().replace("T", "")))
            .max()
            .orElse(1);
    final int oldRelationSize =
        document
            .getRelationEntities()
            .stream()
            .mapToInt(entity -> Integer.parseInt(entity.getTag().replace("R", "")))
            .max()
            .orElse(1);
    final List<Entity> createdEntities = new ArrayList<>();
    final List<RelationEntity> createdRelations = new ArrayList<>();

    for (final FixAnnotationEntity entity : entities) {
      final int entityStart = text.indexOf(entity.getTerm(), start);

      if (entityStart == -1 || entityStart + entity.getTerm().length() > end) {
        throw new InvalidInputException(
            "invalid-fix-entities", "存在不包含在\"" + text + "\"中的entity，请检查表格");
      }

      createdEntities.add(
          new Entity(
              "T" + (oldEntitySize + createdEntities.size() + 1),
              entityStart,
              entityStart + entity.getTerm().length(),
              entity.getType(),
              entity.getTerm()));
    }

    for (final FixAnnotationRelationEntity entity : relations) {
      createdRelations.add(
          new RelationEntity(
              "R" + (oldRelationSize + createdRelations.size() + 1),
              entity.getType(),
              createdEntities.get(entity.getSource()).getTag(),
              createdEntities.get(entity.getTarget()).getTag(),
              "source",
              "target"));
    }

    final List<Entity> oldEntities =
        document.getEntitiesInside(new BratPosition(start, end), false);
    final Map<String, Entity> oldEntityMap = DocumentUtils.getEntityMap(oldEntities);
    final List<RelationEntity> oldRelations = document.getRelationsOutsideToInside(oldEntities);
    final List<RelationEntity> invalidRelations =
        document.getRelationsInside(new BratPosition(start, end), false);
    final String activeTag = createdEntities.get(activeEntity).getTag();

    document.getEntities().removeAll(oldEntities);
    document.getEntities().addAll(createdEntities);
    document.getRelationEntities().addAll(createdRelations);
    document.getRelationEntities().removeAll(invalidRelations);
    oldRelations.forEach(
        relation -> {
          if (oldEntityMap.containsKey(relation.getSourceTag())) {
            relation.setSourceTag(activeTag);
          } else if (oldEntityMap.containsKey(relation.getTargetTag())) {
            relation.setTargetTag(activeTag);
          } else {
            throw new InternalServerException(
                "invalid-relations: outside inside relations不包含source和target");
          }
        });

    annotation.setAnnotation(AnnotationDocumentManipulator.toBratAnnotations(document));
    return Collections.emptyList();
  }

  private Set<Long> getTermAnnotations(final String term, final List<Annotation> annotations) {
    return annotations
        .parallelStream()
        .filter(ann -> StringUtils.contains(ann.getDocument().getText(), term))
        .map(Annotation::getId)
        .collect(Collectors.toSet());
  }

  private boolean isSingleEntitySize(List<EntityListWithPosition> entityLists) {
    return entityLists.parallelStream().allMatch(entityList -> entityList.getEntitySize() == 1);
  }

  private Stream<EntityListWithPosition> getTermEntities(
      final Annotation annotation, final String targetTerm) {
    final AnnotationDocument document = annotation.getDocument();

    if (document.getEntities().size() == 0) {
      return Stream.empty();
    }

    final List<EntityListWithPosition> results = new ArrayList<>();

    int index = -1;
    while ((index = document.getText().indexOf(targetTerm, index)) != -1) {
      final int start = index;
      final int end = index + targetTerm.length();
      final BratPosition position = new BratPosition(start, end);

      if (document
          .getEntities()
          .parallelStream()
          .anyMatch(entity -> entity.intersectWith(start, end))) {
        index = end;
        continue;
      }

      // 找到所有在这个文本范围内的entity
      final List<Entity> entities = document.getEntitiesInside(position, false);
      if (entities.size() == 0) {
        index = end;
        continue;
      }

      final Map<String, Entity> entityMap =
          entities.parallelStream().collect(Collectors.toMap(Entity::getTag, entity -> entity));

      // 找到所有tag以及和这些tag有关的外部关联关系
      final List<RelationEntity> relations = document.getRelationsOutsideToInside(entities);

      if (relations.size() == 0) {
        // 如果没有外部关联，则表示这是一个合法的对应子图
        results.add(new EntityListWithPosition(position, annotation, entities));
      } else {
        final RelationEntity firstRelation = relations.get(0);
        final Entity targetEntity =
            entityMap.get(
                entityMap.containsKey(firstRelation.getSourceTag())
                    ? firstRelation.getSourceTag()
                    : firstRelation.getTargetTag());
        if (relations
            .parallelStream()
            .allMatch(
                relation -> {
                  final Entity entity =
                      entityMap.get(
                          entityMap.containsKey(relation.getSourceTag())
                              ? relation.getSourceTag()
                              : relation.getTargetTag());
                  return entity.getStart() == targetEntity.getStart()
                      && entity.getEnd() == targetEntity.getEnd();
                })) {
          results.add(new EntityListWithPosition(position, annotation, entities));
        }
      }

      index = end;
    }

    return results.stream();
  }

  @lombok.Value
  static class EntityListWithPosition implements AnnotationWithPosition {
    private final BratPosition position;
    private final Annotation annotation;
    private final int entitySize;

    public EntityListWithPosition(
        final BratPosition position, final Annotation annotation, final List<Entity> entities) {
      this.position = position;
      this.annotation = annotation;
      this.entitySize =
          entities
              .stream()
              .map(entity -> new BratPosition(entity.getStart(), entity.getEnd()))
              .collect(Collectors.toSet())
              .size();
    }

    String getTerm() {
      return annotation.getDocument().getText().substring(position.getStart(), position.getEnd());
    }
  }
}
