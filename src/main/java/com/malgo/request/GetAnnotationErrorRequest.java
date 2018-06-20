package com.malgo.request;

import lombok.Data;

@Data
public class GetAnnotationErrorRequest {
  private int annotationType;
  private int startId;
  private int endId;
}
