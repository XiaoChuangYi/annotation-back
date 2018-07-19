package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRelationRequest implements BaseAnnotationRequest {
  private long id;
  private String reTag;
  private String relation;
}
