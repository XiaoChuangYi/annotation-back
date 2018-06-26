package cn.malgo.annotation.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/31. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAnnotationRequest implements BaseAnnotationRequest {

  private String term;
  private String type;
  private int startPosition;
  private int endPosition;
  private String autoAnnotation;
  private int id;
}
