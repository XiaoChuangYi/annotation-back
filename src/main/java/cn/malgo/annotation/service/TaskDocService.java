package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.impl.TaskDocServiceImpl;

public interface TaskDocService {
  TaskDocServiceImpl.AddDocResult addDocToTask(
      AnnotationTask task, OriginalDoc doc, AnnotationTypeEnum annotationType);

  AnnotationTaskDoc updateState(AnnotationTaskDoc taskDoc);

  AnnotationTask updateState(AnnotationTask task);
}
