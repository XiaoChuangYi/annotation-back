package cn.malgo.annotation.utils.entity;

import cn.malgo.annotation.utils.DocumentUtils;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.RelationEntity;
import cn.malgo.core.definition.brat.BratPosition;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnnotationDocument {
  private String text;
  private List<RelationEntity> relationEntities;
  private List<Entity> entities;

  public AnnotationDocument(String text) {
    this.text = text;
  }

  public Map<String, Entity> getEntityMap() {
    return DocumentUtils.getEntityMap(this.entities);
  }

  /**
   * @param position start & end
   * @return 完全包含在position范围内的所有entities
   */
  public List<Entity> getEntitiesInside(final BratPosition position) {
    return entities
        .stream()
        .filter(
            entity ->
                entity.getStart() >= position.getStart() && entity.getEnd() <= position.getEnd())
        .collect(Collectors.toList());
  }

  /**
   * @param position start & end
   * @return 找到所有和position有任何交集的entities
   */
  public Stream<Entity> getEntitiesIntersect(final BratPosition position) {
    return entities
        .stream()
        .filter(
            entity ->
                entity.getEnd() > position.getStart() && entity.getStart() < position.getEnd());
  }

  /**
   * @param position start & end
   * @return 完全包含在position范围内的所有relations
   */
  public List<RelationEntity> getRelationsInside(final BratPosition position) {
    final Map<String, Entity> entityMap = DocumentUtils.getEntityMap(getEntitiesInside(position));
    return relationEntities
        .stream()
        .filter(
            relation ->
                entityMap.containsKey(relation.getSourceTag())
                    && entityMap.containsKey(relation.getTargetTag()))
        .collect(Collectors.toList());
  }

  /**
   * 找到所有不在entities这个子图中的所有关联，即关联的source或target有且仅有一个在entities列表中
   *
   * @param entities
   * @return 不在entities这个子图中的所有关联
   */
  public List<RelationEntity> getRelationsOutsideToInside(final List<Entity> entities) {
    final Map<String, Entity> entityMap = DocumentUtils.getEntityMap(entities);

    return relationEntities
        .stream()
        .filter(
            // 在这里使用^异或表示当前tag的集合对外的所有连接
            // 即source或target只能有一个在集合中
            entity ->
                entityMap.containsKey(entity.getSourceTag())
                    ^ entityMap.containsKey(entity.getTargetTag()))
        .collect(Collectors.toList());
  }

  /**
   * @param entity entity
   * @return entity是否存在关联
   */
  public boolean hasRelation(final Entity entity) {
    return getRelationEntities()
        .stream()
        .anyMatch(
            relation ->
                StringUtils.equalsAny(
                    entity.getTag(), relation.getSourceTag(), relation.getTargetTag()));
  }

  public Entity getEntity(final String tag) {
    return entities.stream().filter(entity -> entity.getTag().equals(tag)).findFirst().orElse(null);
  }

  public RelationEntity getRelation(final String tag) {
    return relationEntities
        .stream()
        .filter(entity -> entity.getTag().equals(tag))
        .findFirst()
        .orElse(null);
  }

  public RelationEntity getRelation(final Entity source, final Entity target) {
    if (source == null || target == null) {
      throw new IllegalArgumentException("getRelation source or target is null");
    }

    return relationEntities
        .stream()
        .filter(
            entity ->
                entity.getSourceTag().equals(source.getTag())
                    && entity.getTargetTag().equals(target.getTag()))
        .findFirst()
        .orElse(null);
  }
}
