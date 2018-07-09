package cn.malgo.annotation.request;

import lombok.Data;

@Data
public class FindAnnotationErrorRequest {
  private int errorType;
  private int startId;
  private int endId;
}
