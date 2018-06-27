package cn.malgo.annotation.request;

import lombok.Getter;
import lombok.Value;

@Value
public class SearchAnnotationRequest {
  private final int annotationType;
  private final int startId;
  private final int endId;

  // term and type are both regex
  private final String term;
  private final String type;
  private final boolean filterFixedErrors;
}
