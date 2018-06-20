package com.malgo.dto;

import com.malgo.utils.entity.AnnotationDocument;

public interface Annotation {
  int getId();

  String getAnnotation();

  void setAnnotation(String annotation);

  AnnotationDocument getDocument();
}
