package cn.malgo.annotation.dto.error;

import lombok.Value;

@Value
public class FixAnnotationEntity {
  private final String type;
  private final String term;
}
