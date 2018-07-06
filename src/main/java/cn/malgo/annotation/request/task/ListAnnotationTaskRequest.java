package cn.malgo.annotation.request.task;

import cn.malgo.annotation.enums.AnnotationTaskState;
import java.util.List;
import lombok.Value;

@Value
public class ListAnnotationTaskRequest {
  int pageIndex;
  int pageSize;
  String name;
  List<AnnotationTaskState> taskStates;
}
