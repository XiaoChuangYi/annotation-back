package cn.malgo.annotation.service;

import cn.malgo.annotation.enums.AnnotationFixLogStateEnum;

public interface FilterAnnotationErrorService {
  AnnotationFixLogStateEnum getState(int annotationId, int start, int end);
}
