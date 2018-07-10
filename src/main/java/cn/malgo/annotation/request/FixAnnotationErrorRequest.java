package cn.malgo.annotation.request;

import cn.malgo.annotation.dto.error.AnnotationErrorContext;
import cn.malgo.annotation.dto.error.FixAnnotationEntity;
import lombok.Value;

import java.util.List;

@Value
public class FixAnnotationErrorRequest {
  private int errorType;
  private List<AnnotationErrorContext> annotations;
  private List<FixAnnotationEntity> entities;
}
