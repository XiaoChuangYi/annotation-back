package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTask;
import java.util.List;

public interface AnnotationSummaryAsyncUpdateService {

  List<AnnotationTask> asyncUpdateAnnotationOverview();

  List<AnnotationTask> asyncUpdateAnnotationEvaluate();
}
