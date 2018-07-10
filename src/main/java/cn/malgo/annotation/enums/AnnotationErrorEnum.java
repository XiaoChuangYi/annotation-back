package cn.malgo.annotation.enums;

import lombok.Getter;

public enum AnnotationErrorEnum {
  // 分词：新词或旧词新意
  NEW_WORD(AnnotationTypeEnum.wordPos),
  // 关联：同一个实体不同类型
  ENTITY_MULTIPLE_TYPE(AnnotationTypeEnum.relation),
  // 关联：孤立实体
  ISOLATED_ENTITY(AnnotationTypeEnum.relation),
  // 关联：实体一致性
  ENTITY_CONSISTENCY(AnnotationTypeEnum.relation),
  // 关联：错误关联类型，source->target的关联type有误
  WRONG_RELATION_TYPE(AnnotationTypeEnum.relation);

  @Getter private AnnotationTypeEnum annotationType;

  AnnotationErrorEnum(AnnotationTypeEnum annotationType) {
    this.annotationType = annotationType;
  }
}
