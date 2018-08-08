package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTask;

public interface AnnotationSummaryService {
  AnnotationTask updateTaskSummary(AnnotationTask task);

  void updatePersonalAnnotatedWordNum(AnnotationTask task);

  void updateAnnotationStateByExpirationTime(AnnotationTask task);

  void updateAnnotationPrecisionAndRecallRate(AnnotationTask task);
}
