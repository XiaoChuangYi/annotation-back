package cn.malgo.annotation.request.task;

import cn.malgo.annotation.enums.AnnotationTaskState;
import java.util.List;
import lombok.Data;
import lombok.Setter;

@Data
public class ListAnnotationTaskRequest {
  int pageIndex;
  int pageSize;
  long taskId;
  String name;
  List<AnnotationTaskState> taskStates;
  boolean all;
}
