package com.malgo.request;

import com.malgo.dto.AnnotationErrorContext;
import com.malgo.dto.FixAnnotationEntity;
import lombok.Value;

import java.util.List;

@Value
public class FixAnnotationErrorRequest {
  private List<AnnotationErrorContext> annotations;
  private List<FixAnnotationEntity> entities;
}
