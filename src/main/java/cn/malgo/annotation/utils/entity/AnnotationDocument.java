package cn.malgo.annotation.utils.entity;

import cn.malgo.core.definition.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
}
