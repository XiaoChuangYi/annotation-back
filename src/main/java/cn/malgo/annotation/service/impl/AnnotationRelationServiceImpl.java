package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.utils.AnnotationConvert;
import org.springframework.stereotype.Service;

@Service("relation")
public class AnnotationRelationServiceImpl extends BaseAnnotationOperateImpl {
  public AnnotationRelationServiceImpl(
      AnnotationFactory factory, AnnotationCombineRepository annotationCombineRepository) {
    super(factory, annotationCombineRepository);
  }

  @Override
  protected String addAnnotation(final Annotation annotation, final AddAnnotationRequest request) {
    return AnnotationConvert.addRelationEntitiesAnnotation(
        annotation.getAnnotation(),
        request.getType(),
        request.getStartPosition(),
        request.getEndPosition(),
        request.getTerm());
  }
}
