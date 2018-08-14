package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteAnnotationGroupRequest implements BaseAnnotationRequest {
  private long id;
  private String tag;
  private int startPosition;
  private int endPosition;
  private String term;
  private String reTag;
}
