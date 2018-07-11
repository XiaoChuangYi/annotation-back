package cn.malgo.annotation.dto.error;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.core.definition.brat.BratPosition;

public interface AnnotationWithPosition {
  Annotation getAnnotation();

  BratPosition getPosition();
}
