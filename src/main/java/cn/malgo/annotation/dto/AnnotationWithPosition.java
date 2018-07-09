package cn.malgo.annotation.dto;

import cn.malgo.core.definition.brat.BratPosition;

public interface AnnotationWithPosition {
  Annotation getAnnotation();

  BratPosition getPosition();
}
