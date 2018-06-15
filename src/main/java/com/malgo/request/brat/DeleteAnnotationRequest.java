package com.malgo.request.brat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/31. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteAnnotationRequest implements BaseAnnotationRequest {

  private int id;
  private String tag;
  private String autoAnnotation;
}
