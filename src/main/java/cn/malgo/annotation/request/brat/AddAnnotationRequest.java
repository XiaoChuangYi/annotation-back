package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAnnotationRequest implements BaseAnnotationRequest {
  private long id;
  private String term;
  private String type;
  private int startPosition;
  private int endPosition;
  private String autoAnnotation;
}
