package cn.malgo.annotation.request.task;

import lombok.Value;

@Value
public class GetUnCoveredBlockRequest {
  private int presupposePageSize;
  private double threshold;
}
