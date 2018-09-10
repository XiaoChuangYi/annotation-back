package cn.malgo.annotation.enums;

public enum AnnotationTypeEnum {
  wordPos("分词"), // 0
  sentence("分句"), // 1
  relation("关联"), // 2
  disease("疾病");

  private final String name;

  AnnotationTypeEnum(final String name) {
    this.name = name;
  }

  public static AnnotationTypeEnum getByValue(int value) {
    if (value < 0 || value >= values().length) {
      return null;
    }

    return values()[value];
  }
}
