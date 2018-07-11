package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddAnnotationGroupRequest implements BaseAnnotationRequest {

  private int id;
  private String term;
  private String type;
  private int startPosition;
  private int endPosition;
  private String sourceTag;
  private String targetTag;
  private String relation;
}
