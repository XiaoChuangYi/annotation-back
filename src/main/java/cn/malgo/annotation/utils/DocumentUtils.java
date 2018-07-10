package cn.malgo.annotation.utils;

import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.annotation.utils.entity.RelationEntity;
import cn.malgo.core.definition.Entity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DocumentUtils {
  public static int getMinStart(
      final AnnotationDocument document, final List<RelationEntity> relations) {
    final Map<String, Entity> entityMap = document.getEntityMap();
    return relations
        .stream()
        .mapToInt(
            relation ->
                Math.min(
                    entityMap.get(relation.getSourceTag()).getStart(),
                    entityMap.get(relation.getTargetTag()).getStart()))
        .min()
        .orElse(Integer.MAX_VALUE);
  }

  public static int getMaxEnd(
      final AnnotationDocument document, final List<RelationEntity> relations) {
    final Map<String, Entity> entityMap = document.getEntityMap();
    return relations
        .stream()
        .mapToInt(
            relation ->
                Math.max(
                    entityMap.get(relation.getSourceTag()).getEnd(),
                    entityMap.get(relation.getTargetTag()).getEnd()))
        .max()
        .orElse(Integer.MIN_VALUE);
  }

  public static Map<String, Entity> getEntityMap(final List<Entity> entities) {
    return entities.stream().collect(Collectors.toMap(Entity::getTag, entity -> entity));
  }
}
