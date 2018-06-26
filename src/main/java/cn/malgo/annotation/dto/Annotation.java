package cn.malgo.annotation.dto;

import cn.malgo.annotation.utils.entity.AnnotationDocument;

public interface Annotation {
  int getId();

  String getAnnotation();

  void setAnnotation(String annotation);

  AnnotationDocument getDocument();
}
