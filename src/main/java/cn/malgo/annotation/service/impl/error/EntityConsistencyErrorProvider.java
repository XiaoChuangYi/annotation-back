package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.AnnotationWithPosition;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.annotation.utils.entity.RelationEntity;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            .stream()
            .flatMap(
                annotation -> annotation.getDocument().getEntities().stream().map(Entity::getTerm))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet())
            .stream()
            .sorted(comparator)
            .collect(Collectors.toList());

    for (final String term : terms) {
      final List<EntityListWithPosition> entityLists =
          annotations
              .stream()
              .flatMap(annotation -> this.getTermEntities(annotation, term))
              .collect(Collectors.toList());

      if (!entityLists.stream().allMatch(entityList -> entityList.getEntities().size() == 1)) {
        // 如果找到的entityList中存在不是整个子串的，则表示有不一致性
        final List<EntityListWithPosition> filtered =
            filterErrors(entityLists).collect(Collectors.toList());
        if (!filtered.stream().allMatch(entityList -> entityList.getEntities().size() == 1)) {
          return postProcess(
              filtered
                  .stream()
                  .map(
                      entityList ->
                          new WordErrorWithPosition(
                              term,
                              entityList.getEntities().size() == 1 ? "单一实体" : "子图",
                              entityList.getPosition(),
                              entityList.getAnnotation()))
                  .collect(Collectors.toList()),
              0);
        }
      }
    }

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

      if (document.getEntities().stream().anyMatch(entity -> entity.intersectWith(start, end))) {
        index = end;
        continue;
      }

      // 找到所有在这个文本范围内的entity
      final List<Entity> entities = document.getEntitiesInside(position);
      final Map<String, Entity> entityMap =
          entities.stream().collect(Collectors.toMap(Entity::getTag, entity -> entity));

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
            .stream()
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
