package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.Entity;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockExportEntityVO {

  private List<EntityInfo> dataList;

  @Value
  public static class EntityInfo {

    private long id;
    private String text;
    private List<EntityDetail> entities;

    public EntityInfo(AnnotationTaskBlock annotationTaskBlock) {
      final List<Entity> entities =
          AnnotationConvert.getEntitiesFromAnnotation(annotationTaskBlock.getAnnotation());
      this.id = annotationTaskBlock.getId();
      this.text = annotationTaskBlock.getText();
      this.entities =
          entities
              .parallelStream()
              .map(
                  entity ->
                      new EntityDetail(
                          entity.getTerm(), entity.getType(), entity.getStart(), entity.getEnd()))
              .collect(Collectors.toList());
    }

    @Value
    static class EntityDetail {

      private String name;
      private String type;
      private int start;
      private int end;
    }
  }
}
