package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import java.util.Map;

public interface AnnotationSummaryService {
  AnnotationTask updateTaskSummary(long id);

  void updateTaskPersonalSummary(AnnotationTask task);

  void updateAnnotationStateByExpirationTime(AnnotationTask task);

  void updateAnnotationPrecisionAndRecallRate(
      AnnotationNew annotation, Map<Long, AnnotationTaskBlock> blockMap);

  void asyUpdateAnnotationStaffEvaluate(AnnotationTask task);
}
