package cn.malgo.annotation.dto;

import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.utils.entity.AnnotationDocument;

public interface Annotation {
  long getId();

  AnnotationTypeEnum getAnnotationType();

  String getAnnotation();

  void setAnnotation(String annotation);

  AnnotationDocument getDocument();
}
