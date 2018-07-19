package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddRelationRequest implements BaseAnnotationRequest {
  private long id;
  private String sourceTag;
  private String targetTag;
  private String relation;
}
