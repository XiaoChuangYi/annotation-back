package cn.malgo.annotation.request.task;

import lombok.Value;

@Value
public class TerminateTaskRequest {
  private long taskId;
}
