package cn.malgo.annotation.request.task;

import lombok.Data;
import lombok.Value;

import java.util.List;

@Data
public class ListAnnotationTaskBlockRequest {
  private int taskId;
  private int pageIndex;
  private int pageSize;
  private String text;
  private Boolean regexMode;
  private Integer id;
  private List<Integer> annotationTypes;
  private List<String> states;
}
