package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/31. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRelationRequest implements BaseAnnotationRequest {
  private int id;
  private String reTag;
  private String relation;
}
