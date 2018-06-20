package com.malgo.service.impl;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/6/12. */
@Service("relation")
public class AnnotationRelationServiceImpl extends BaseAnnotationOperateImpl {

  public AnnotationRelationServiceImpl(
      AnnotationCombineRepository annotationCombineRepository) {
    super(annotationCombineRepository);
  }

  @Override
  public String addAnnotation(AnnotationCombineRepository annotationCombineRepository,
      AddAnnotationRequest addAnnotationRequest, int roleId) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String newAnnotation=
          AnnotationConvert.addRelationEntitiesAnnotation(
              annotationCombine.getManualAnnotation(),
              addAnnotationRequest.getType(),
              addAnnotationRequest.getStartPosition(),
              addAnnotationRequest.getEndPosition(),
              addAnnotationRequest.getTerm());
      annotationCombine.setManualAnnotation(newAnnotation);
      annotationCombineRepository.save(annotationCombine);
      return newAnnotation;
    }
    return "";
  }
}