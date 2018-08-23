package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;

public interface AnnotationSummaryService {
  AnnotationTask updateTaskSummary(long id);

  void updateTaskPersonalSummary(AnnotationTask task);

  void updateAnnotationStateByExpirationTime(AnnotationTask task);

  void updateAnnotationPrecisionAndRecallRate(AnnotationNew annotation);

  void asyUpdateAnnotationStaffEvaluate(AnnotationTask task);
}
