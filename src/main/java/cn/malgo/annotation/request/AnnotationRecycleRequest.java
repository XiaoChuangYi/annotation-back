package cn.malgo.annotation.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationRecycleRequest {
  private List<Long> annotationIdList;
}
