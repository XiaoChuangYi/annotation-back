package cn.malgo.annotation.enums;

import lombok.Getter;

public enum AnnotationErrorEnum {
  // 分词：新词或旧词新意
  NEW_WORD(AnnotationTypeEnum.wordPos, true, false),
  // 关联：同一个实体不同类型
  ENTITY_MULTIPLE_TYPE(AnnotationTypeEnum.relation, true, true),
  // 关联：孤立实体
  ISOLATED_ENTITY(AnnotationTypeEnum.relation, false, true),
  // 关联：实体一致性
  ENTITY_CONSISTENCY(AnnotationTypeEnum.relation, true, true),
  // 关联：错误关联类型，source->target的关联type有误
  WRONG_RELATION_TYPE(AnnotationTypeEnum.relation, false, true);

  @Getter private final AnnotationTypeEnum annotationType;
  @Getter private final boolean canFix;
  @Getter private final boolean canReset;

  AnnotationErrorEnum(
      final AnnotationTypeEnum annotationType, final boolean canFix, final boolean canReset) {
    this.annotationType = annotationType;
    this.canFix = canFix;
    this.canReset = canReset;
  }
}
