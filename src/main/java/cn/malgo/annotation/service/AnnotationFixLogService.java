package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationFixLog;
import cn.malgo.annotation.enums.AnnotationFixLogStateEnum;

public interface AnnotationFixLogService {
  AnnotationFixLog insertOrUpdate(
      long annotationId, int start, int end, AnnotationFixLogStateEnum state);
}
