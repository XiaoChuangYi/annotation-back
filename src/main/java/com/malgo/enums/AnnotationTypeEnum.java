package com.malgo.enums;

/** Created by cjl on 2018/6/14. */
public enum AnnotationTypeEnum {
  wordPos(0, "分词"),
  sentence(1, "分句"),
  relation(2, "关联");

  private final int value;
  private final String name;

  AnnotationTypeEnum(int value, String name) {
    this.value = value;
    this.name = name;
  }

  public int getValue() {
    return this.value;
  }

  public static AnnotationTypeEnum getByValue(int value) {
    for (AnnotationTypeEnum current : values()) {
      if (current.value == value) {
        return current;
      }
    }
    return null;
  }
}
