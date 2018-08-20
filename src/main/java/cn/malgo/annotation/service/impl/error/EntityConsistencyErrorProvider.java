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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EntityConsistencyErrorProvider extends BaseErrorProvider {
  public EntityConsistencyErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository) {
    super(annotationFixLogRepository);
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

    for (final String term : terms) {
      final List<EntityListWithPosition> entityLists =
          annotations
              .parallelStream()
              .flatMap(annotation -> this.getTermEntities(annotation, term))
              .collect(Collectors.toList());

      if (!entityLists
          .parallelStream()
          .allMatch(entityList -> entityList.getEntities().size() == 1)) {
        // 如果找到的entityList中存在不是整个子串的，则表示有不一致性
        final List<EntityListWithPosition> filtered =
            filterErrors(entityLists).collect(Collectors.toList());
        if (filtered
                .parallelStream()
                .map(entityList -> entityList.getEntities().size())
                .collect(Collectors.toSet())
                .size()
            != 1) {
          return postProcess(
              filtered
                  .parallelStream()
                  .map(
                      entityList ->
                          new WordErrorWithPosition(
                              term,
                              entityList.getEntities().size() == 1 ? "单一实体" : "子图",
                              entityList.getPosition(),
                              entityList.getAnnotation(),
                              null))
                  .collect(Collectors.toList()),
              0);
        }
      }
    }

    return Collections.emptyList();
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

    final List<Entity> oldEntities = document.getEntitiesInside(new BratPosition(start, end));
    final Map<String, Entity> oldEntityMap = DocumentUtils.getEntityMap(oldEntities);
    final List<RelationEntity> oldRelations = document.getRelationsOutsideToInside(oldEntities);
    final List<RelationEntity> invalidRelations =
        document.getRelationsInside(new BratPosition(start, end));
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

  private Stream<EntityListWithPosition> getTermEntities(
      final Annotation annotation, final String targetTerm) {
    final AnnotationDocument document = annotation.getDocument();

    if (!StringUtils.contains(document.getText(), targetTerm)) {
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
      final List<Entity> entities = document.getEntitiesInside(position);
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
    private final List<Entity> entities;
  }
}