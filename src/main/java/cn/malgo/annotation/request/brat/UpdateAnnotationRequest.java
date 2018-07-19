package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAnnotationRequest implements BaseAnnotationRequest {
  private long id;
  private String tag;
  private String newType;
  private String autoAnnotation;
  private int startPosition;
  private int endPosition;
  private String term;
}
