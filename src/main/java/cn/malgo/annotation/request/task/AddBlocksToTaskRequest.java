package cn.malgo.annotation.request.task;

import java.util.List;
import lombok.Value;

@Value
public class AddBlocksToTaskRequest {
  private long taskId;
  private List<Long> blockIds;
}
