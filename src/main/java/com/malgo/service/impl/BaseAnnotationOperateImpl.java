package com.malgo.service.impl;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by cjl on 2018/6/14.
 */
public abstract class BaseAnnotationOperateImpl implements AnnotationOperateService {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public BaseAnnotationOperateImpl(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  public abstract String addAnnotation(AnnotationCombineRepository annotationCombineRepository,AddAnnotationRequest addAnnotationRequest, int roleId);


  @Override
  public String addAnnotation(AddAnnotationRequest addAnnotationRequest,int roleId){
    return addAnnotation(annotationCombineRepository,addAnnotationRequest,roleId);
  }

  @Override
  public String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest) {
    Optional<AnnotationCombine> optional=
        annotationCombineRepository.findById(deleteAnnotationRequest.getId());
    if(optional.isPresent()){
      AnnotationCombine annotationCombine=optional.get();
      String annotation=
          AnnotationConvert.deleteEntitiesAnnotation(annotationCombine.getManualAnnotation(),deleteAnnotationRequest.getTag());
      annotationCombine.setManualAnnotation(annotation);
      annotationCombineRepository.save(annotationCombine);
      return annotation;
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
      annotationCombine.setManualAnnotation(newAnnotation);
      annotationCombineRepository.save(annotationCombine);
      return newAnnotation;
    }
    return "";
  }
}
