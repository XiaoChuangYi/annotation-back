package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
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
  private final AnnotationRepository annotationRepository;

  @Autowired
  public RelationOperateServiceImpl(
      AnnotationFactory factory, AnnotationRepository annotationRepository) {
    this.factory = factory;
    this.annotationRepository = annotationRepository;
  }

  private Annotation process(AnnotationNew annotationNew, Function<Annotation, String> operator) {
    if (annotationNew.getState() == AnnotationStateEnum.PRE_ANNOTATION) {
      annotationNew.setState(AnnotationStateEnum.ANNOTATION_PROCESSING);
    }

    final Annotation annotation = this.factory.create(annotationNew);
    annotation.setAnnotation(operator.apply(annotation));
    annotationRepository.save(annotationNew);
    return annotation;
  }

  @Override
  public String addRelation(final AnnotationNew annotationNew, final AddRelationRequest request) {
    return process(
            annotationNew,
            (annotation) ->
                AnnotationConvert.addRelationsAnnotation(
                    annotation.getAnnotation(),
                    request.getSourceTag(),
                    request.getTargetTag(),
                    request.getRelation()))
        .getAnnotation();
  }

  @Override
  public String updateRelation(AnnotationNew annotationNew, UpdateRelationRequest request) {

    return process(
            annotationNew,
            (annotation) ->
                AnnotationConvert.updateRelationAnnotation(
                    annotation.getAnnotation(), request.getReTag(), request.getRelation()))
        .getAnnotation();
  }

  @Override
  public String deleteRelation(AnnotationNew annotationNew, DeleteRelationRequest request) {
    return process(
            annotationNew,
            annotation ->
                AnnotationConvert.deleteRelationsAnnotation(
                    annotation.getAnnotation(), request.getReTag()))
        .getAnnotation();
  }
}
