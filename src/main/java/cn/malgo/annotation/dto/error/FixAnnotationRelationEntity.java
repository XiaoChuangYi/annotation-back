package cn.malgo.annotation.dto.error;

import lombok.Value;

@Value
public class FixAnnotationRelationEntity {
  private final int source;
  private final int target;
  private final String type;
}
