package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.AnnotationTask;
import lombok.Value;

@Value
public class AddDocsToTaskResponse {
  private final AnnotationTask annotationTask;
  private final int createdBlocks;
}
