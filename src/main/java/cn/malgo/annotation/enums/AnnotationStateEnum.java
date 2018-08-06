package cn.malgo.annotation.enums;

public enum AnnotationStateEnum {
  UN_DISTRIBUTED,
  PRE_ANNOTATION,
  ANNOTATION_PROCESSING,
  SUBMITTED,
  PRE_CLEAN,
  CLEANED;

  //  private String name;
  //
  //  AnnotationStateEnum(String name) {
  //    this.name = name;
  //  }
  //
  //  public static AnnotationStateEnum getByValue(int value) {
  //    if (value < 0 || value >= values().length) {
  //      return null;
  //    }
  //    return values()[value];
  //  }
}
