package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.utils.AnnotationConvert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("local")
@Slf4j
public class AnnotationOperateServiceImpl extends BaseAnnotationOperateImpl {
  public AnnotationOperateServiceImpl(AnnotationFactory factory, AnnotationCombineRepository repo) {
    super(factory, repo);
  }

  @Override
  protected String addAnnotation(final Annotation annotation, final AddAnnotationRequest request) {
    return AnnotationConvert.addEntitiesAnnotation(
        annotation.getAnnotation(),
        request.getType(),
        request.getStartPosition(),
        request.getEndPosition(),
        request.getTerm());
  }
}
