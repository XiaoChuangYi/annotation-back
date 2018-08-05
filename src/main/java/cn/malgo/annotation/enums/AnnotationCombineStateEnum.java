package cn.malgo.annotation.enums;

public enum AnnotationCombineStateEnum {
  unDistributed("未分配"),
  preAnnotation("待标注"),
  annotationProcessing("标注中"),
  preExamine("待审核"),
  abandon("已放弃"),
  innerAnnotation("内部标注"),
  errorPass("纠错通过"),
  examinePass("审核通过"),
  UNKNOWN("未知");

  private String message;

  AnnotationCombineStateEnum(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
