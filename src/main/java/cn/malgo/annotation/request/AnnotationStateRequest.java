package cn.malgo.annotation.request;

import java.util.List;
import lombok.Value;

@Value
public class AnnotationStateRequest {
  private long id;
  private List<Long> ids;
}
