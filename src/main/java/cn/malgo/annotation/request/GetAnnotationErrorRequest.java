package cn.malgo.annotation.request;

import lombok.Data;

@Data
public class GetAnnotationErrorRequest {
  private int annotationType;
  private int startId;
  private int endId;
}
