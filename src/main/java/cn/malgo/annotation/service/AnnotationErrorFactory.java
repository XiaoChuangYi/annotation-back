package cn.malgo.annotation.service;

import cn.malgo.annotation.enums.AnnotationErrorEnum;

public interface AnnotationErrorFactory {
  AnnotationErrorProvider getProvider(AnnotationErrorEnum errorEnum);
}
