package com.malgo.service;

import com.malgo.dto.Annotation;
import com.malgo.entity.AnnotationCombine;

public interface AnnotationFactory {
  Annotation create(AnnotationCombine annotationCombine);
}
