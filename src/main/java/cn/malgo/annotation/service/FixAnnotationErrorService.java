package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.FixAnnotationEntity;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.core.definition.Entity;

import java.util.List;

public interface FixAnnotationErrorService {
  List<Entity> fixAnnotationError(
      AnnotationErrorEnum errorType,
      Annotation annotation,
      int start,
      int end,
      List<FixAnnotationEntity> entities);
}
