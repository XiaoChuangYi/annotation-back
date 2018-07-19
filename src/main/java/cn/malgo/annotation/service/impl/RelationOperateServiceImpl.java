package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service("task-relation")
public class RelationOperateServiceImpl implements RelationOperateService {
  private final AnnotationFactory factory;
  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public RelationOperateServiceImpl(
      AnnotationFactory factory, AnnotationCombineRepository annotationCombineRepository) {
    this.factory = factory;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  private Annotation process(
      AnnotationCombine annotationCombine, Function<Annotation, String> operator) {
    if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
      annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
    }

    final Annotation annotation = this.factory.create(annotationCombine);
    annotation.setAnnotation(operator.apply(annotation));
    annotationCombineRepository.save(annotationCombine);
    return annotation;
  }

  @Override
  public String addRelation(
      final AnnotationCombine annotationCombine, final AddRelationRequest request) {
    return process(
            annotationCombine,
            (annotation) ->
                AnnotationConvert.addRelationsAnnotation(
                    annotation.getAnnotation(),
                    request.getSourceTag(),
                    request.getTargetTag(),
                    request.getRelation()))
        .getAnnotation();
  }

  @Override
  public String updateRelation(AnnotationCombine annotationCombine, UpdateRelationRequest request) {

    return process(
            annotationCombine,
            (annotation) ->
                AnnotationConvert.updateRelationAnnotation(
                    annotation.getAnnotation(), request.getReTag(), request.getRelation()))
        .getAnnotation();
  }

  @Override
  public String deleteRelation(AnnotationCombine annotationCombine, DeleteRelationRequest request) {
    return process(
            annotationCombine,
            annotation ->
                AnnotationConvert.deleteRelationsAnnotation(
                    annotation.getAnnotation(), request.getReTag()))
        .getAnnotation();
  }
}
