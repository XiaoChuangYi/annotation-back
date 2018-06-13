package com.malgo.service.impl;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.service.AnnotationRelationService;
import com.malgo.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/6/12. */
@Service
public class AnnotationRelationServiceImpl implements AnnotationRelationService {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public AnnotationRelationServiceImpl(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  public String addAnnotation(AddAnnotationRequest addAnnotationRequest, int roleId) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String newAnnotation;
      //      if (roleId > 2) {
      //        newAnnotation =
      //            AnnotationConvert.addRelationEntitiesAnnotation(
      //                annotationCombine.getManualAnnotation(),
      //                addAnnotationRequest.getType(),
      //                addAnnotationRequest.getStartPosition(),
      //                addAnnotationRequest.getEndPosition(),
      //                addAnnotationRequest.getTerm());
      //      }
      //      if (roleId > 0 && roleId <= 2) {
      newAnnotation =
          AnnotationConvert.addRelationEntitiesAnnotation(
              annotationCombine.getManualAnnotation(),
              addAnnotationRequest.getType(),
              addAnnotationRequest.getStartPosition(),
              addAnnotationRequest.getEndPosition(),
              addAnnotationRequest.getTerm());
      //      }
      return newAnnotation;
    }
    return "";
  }

  @Override
  public String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest, int roleId) {
    return null;
  }

  @Override
  public String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest, int roleId) {
    return null;
  }
}
