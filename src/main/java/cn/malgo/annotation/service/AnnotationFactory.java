package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;

public interface AnnotationFactory {

  Annotation create(AnnotationTaskBlock block);

  Annotation create(AnnotationNew annotation);
}
