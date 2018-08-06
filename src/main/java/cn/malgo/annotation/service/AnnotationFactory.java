package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;

public interface AnnotationFactory {
  Annotation create(AnnotationCombine annotationCombine);

  Annotation create(AnnotationTaskBlock block);

  Annotation create(AnnotationNew annotation);
}
