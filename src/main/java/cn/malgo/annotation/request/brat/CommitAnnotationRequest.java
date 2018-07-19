package cn.malgo.annotation.request.brat;

import lombok.Data;

@Data
public class CommitAnnotationRequest {
  private long id;
  private String autoAnnotation;
}
