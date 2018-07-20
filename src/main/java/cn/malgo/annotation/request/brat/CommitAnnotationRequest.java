package cn.malgo.annotation.request.brat;

import lombok.Value;

@Value
public class CommitAnnotationRequest {
  private long id;
  private String autoAnnotation;
}
