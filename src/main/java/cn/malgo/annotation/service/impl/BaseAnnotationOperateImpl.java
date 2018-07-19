package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;

public abstract class BaseAnnotationOperateImpl implements AnnotationOperateService {
  private final AnnotationFactory factory;
  private final AnnotationCombineRepository annotationCombineRepository;

  public BaseAnnotationOperateImpl(
      AnnotationFactory factory, AnnotationCombineRepository annotationCombineRepository) {
    this.factory = factory;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  private Annotation getAnnotation(AnnotationCombine annotationCombine) {
    return factory.create(annotationCombine);
  }

  protected abstract String addAnnotation(Annotation annotation, AddAnnotationRequest request);

  @Override
  public String addAnnotation(
      final AnnotationCombine annotationCombine, final AddAnnotationRequest request) {
    if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
      annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
    }

    final Annotation annotation = getAnnotation(annotationCombine);
    annotation.setAnnotation(addAnnotation(annotation, request));
    annotationCombineRepository.save(annotationCombine);
    return annotation.getAnnotation();
  }

  @Override
  public String deleteAnnotation(
      final AnnotationCombine annotationCombine, final DeleteAnnotationRequest request) {
    if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
      annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
    }

    final Annotation annotation = getAnnotation(annotationCombine);
    annotation.setAnnotation(
        AnnotationConvert.deleteEntitiesAnnotation(annotation.getAnnotation(), request.getTag()));
    annotationCombineRepository.save(annotationCombine);
    return annotation.getAnnotation();
  }

  @Override
  public String updateAnnotation(
      final AnnotationCombine annotationCombine, final UpdateAnnotationRequest request) {
    if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
      annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
    }

    final Annotation annotation = getAnnotation(annotationCombine);
    annotation.setAnnotation(
        AnnotationConvert.updateEntitiesAnnotation(
            annotation.getAnnotation(), request.getTag(), request.getNewType()));
    annotationCombineRepository.save(annotationCombine);
    return annotation.getAnnotation();
  }
}
