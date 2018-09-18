package cn.malgo.annotation.request.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminateTaskRequest {
  private long taskId;
  private boolean forceTerminate;

  public TerminateTaskRequest(final long taskId) {
    this.taskId = taskId;
    this.forceTerminate = false;
  }
}
