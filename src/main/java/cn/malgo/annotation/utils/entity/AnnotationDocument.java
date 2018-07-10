package cn.malgo.annotation.utils.entity;

import cn.malgo.annotation.utils.DocumentUtils;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
