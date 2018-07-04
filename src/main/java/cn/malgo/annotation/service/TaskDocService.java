package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;

public interface TaskDocService {
  AnnotationTaskDoc addDocToTask(
      AnnotationTask task, OriginalDoc doc, AnnotationTypeEnum annotationType);
}
