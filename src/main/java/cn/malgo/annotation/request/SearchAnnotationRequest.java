package cn.malgo.annotation.request;

import lombok.Data;
import lombok.Value;

@Data
public class SearchAnnotationRequest {
  private final int annotationType;

  // term and type are both regex
  private final String term;
  private final String type;
}
