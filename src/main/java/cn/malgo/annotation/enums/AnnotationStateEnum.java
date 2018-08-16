package cn.malgo.annotation.enums;

public enum AnnotationStateEnum {
  // 待分配
  UN_DISTRIBUTED,
  // 待标注
  PRE_ANNOTATION,
  // 标注中
  ANNOTATION_PROCESSING,
  // 已提交
  SUBMITTED,
  // 待清洗
  PRE_CLEAN,
  // 已清洗
  CLEANED;
}
