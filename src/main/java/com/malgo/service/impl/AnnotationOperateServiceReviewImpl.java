package com.malgo.service.impl;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.utils.AnnotationConvert;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/5/31. */
@Service("local-review")
@Slf4j
public class AnnotationOperateServiceReviewImpl implements AnnotationOperateService {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public AnnotationOperateServiceReviewImpl(
      AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  public String addAnnotation(AddAnnotationRequest addAnnotationRequest, int roleId) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String newAnnotation =
          AnnotationConvert.addEntitiesAnnotation(
              annotationCombine.getManualAnnotation(),
              addAnnotationRequest.getType(),
              addAnnotationRequest.getStartPosition(),
              addAnnotationRequest.getEndPosition(),
              addAnnotationRequest.getTerm());
      annotationCombine.setManualAnnotation(newAnnotation);

      return newAnnotation;
    }
    return "";
  }

  @Override
  public String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(deleteAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String newAnnotation =
          AnnotationConvert.deleteEntitiesAnnotation(
              annotationCombine.getManualAnnotation(), deleteAnnotationRequest.getTag());
      return newAnnotation;
    }
    return "";
  }

  @Override
  public String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(updateAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String newAnnotation =
          AnnotationConvert.updateEntitiesAnnotation(
              annotationCombine.getManualAnnotation(),
              updateAnnotationRequest.getTag(),
              updateAnnotationRequest.getNewType());
      return newAnnotation;
    }
    return "";
  }

  @Override
  public void test() {
    log.info("local-review");
  }
}
