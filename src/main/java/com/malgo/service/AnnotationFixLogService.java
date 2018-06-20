package com.malgo.service;

import com.malgo.entity.AnnotationFixLog;
import com.malgo.enums.AnnotationFixLogStateEnum;

public interface AnnotationFixLogService {
  AnnotationFixLog insertOrUpdate(
      int annotationId, int start, int end, AnnotationFixLogStateEnum state);
}
