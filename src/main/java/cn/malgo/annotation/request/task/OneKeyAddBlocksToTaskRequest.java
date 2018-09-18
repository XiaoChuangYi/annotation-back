package cn.malgo.annotation.request.task;

import java.util.List;
import lombok.Data;
import lombok.Value;

@Data
public class OneKeyAddBlocksToTaskRequest {
  private int totalWordNum;
  private int threshold = 0;
  private long taskId;
  private List<String> annotationTypes;
}
