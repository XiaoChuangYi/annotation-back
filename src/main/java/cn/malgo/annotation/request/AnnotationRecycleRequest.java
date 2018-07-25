package cn.malgo.annotation.request;

import java.util.List;
import lombok.Value;

@Value
public class AnnotationRecycleRequest {
  private List<Long> annotationIdList;
}
