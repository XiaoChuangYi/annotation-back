package cn.malgo.annotation.request.task;

import java.util.List;
import lombok.Value;

@Value
public class ListAnnotationTaskBlockRequest {
  private int pageIndex;
  private int pageSize;
  private List<Integer> annotationTypes;
  private String text;
  private List<String> states;
}
