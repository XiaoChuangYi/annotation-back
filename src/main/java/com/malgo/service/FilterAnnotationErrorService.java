package com.malgo.service;

import com.malgo.enums.AnnotationFixLogStateEnum;

public interface FilterAnnotationErrorService {
  AnnotationFixLogStateEnum getState(int annotationId, int start, int end);
}
