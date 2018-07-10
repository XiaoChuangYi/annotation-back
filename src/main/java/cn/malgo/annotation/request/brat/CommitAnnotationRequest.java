package cn.malgo.annotation.request.brat;

import lombok.Data;

@Data
public class CommitAnnotationRequest {
  private int id;
  private String autoAnnotation;
}
