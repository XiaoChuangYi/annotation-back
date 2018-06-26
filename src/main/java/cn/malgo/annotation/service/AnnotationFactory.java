package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;

public interface AnnotationFactory {
  Annotation create(AnnotationCombine annotationCombine);
}
