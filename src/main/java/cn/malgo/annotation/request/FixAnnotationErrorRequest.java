package cn.malgo.annotation.request;

import cn.malgo.annotation.dto.AnnotationErrorContext;
import cn.malgo.annotation.dto.FixAnnotationEntity;
import lombok.Value;

import java.util.List;

@Value
public class FixAnnotationErrorRequest {
  private List<AnnotationErrorContext> annotations;
  private List<FixAnnotationEntity> entities;
}
