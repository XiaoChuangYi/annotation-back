package cn.malgo.annotation.request.task;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBlocksToTaskRequest {
  private long taskId;
  private List<Long> blockIds;
}
