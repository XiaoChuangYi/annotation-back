package cn.malgo.annotation.request.task;

import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import java.util.List;
import lombok.Value;

@Value
public class ListAnnotationTaskBlockRequest {
  int pageIndex;
  int pageSize;
  List<String> annotationTypes;
  String text;
  List<AnnotationTaskState> states;
}
