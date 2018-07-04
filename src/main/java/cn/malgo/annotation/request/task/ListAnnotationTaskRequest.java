package cn.malgo.annotation.request.task;

import java.util.List;
import lombok.Value;

@Value
public class ListAnnotationTaskRequest {
  private int pageIndex;
  private int pageSize;
  private String name;
  private List<String> taskState;
}
