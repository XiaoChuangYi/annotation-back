package cn.malgo.annotation.request.task;

import lombok.Value;

@Value
public class OneKeyAddBlocksToTaskRequest {
  private int totalWordNum;
  private int threshold = 0;
  private long taskId;
}
