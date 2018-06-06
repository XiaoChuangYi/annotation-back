package com.malgo.service.impl;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.request.brat.AddRelationRequest;
import com.malgo.request.brat.DeleteRelationRequest;
import com.malgo.request.brat.UpdateRelationRequest;
import com.malgo.service.RelationOperateService;
import com.malgo.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by cjl on 2018/5/31.
 */
@Service("review")
public class RelationOperateServiceReviewImpl implements RelationOperateService {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public RelationOperateServiceReviewImpl(AnnotationCombineRepository annotationCombineRepository){
    this.annotationCombineRepository=annotationCombineRepository;
  }

  @Override
  public String addRelation(AddRelationRequest addRelationRequest) {
    Optional<AnnotationCombine> optional =annotationCombineRepository.findById(addRelationRequest.getId());
    if(optional.isPresent()){
      AnnotationCombine annotationCombine=optional.get();
      String annotation= AnnotationConvert.addRelationsAnnotation(annotationCombine.getReviewedAnnotation(),
          addRelationRequest.getSourceTag(),addRelationRequest.getTargetTag(),addRelationRequest.getRelation());
      return annotation;
    }
    return "";
  }

  @Override
  public String updateRelation(UpdateRelationRequest updateRelationRequest) {
    Optional<AnnotationCombine> optional =annotationCombineRepository.findById(updateRelationRequest.getId());
    if(optional.isPresent()){
      AnnotationCombine annotationCombine=optional.get();
      String annotation=AnnotationConvert.updateRelationAnnotation(annotationCombine.getReviewedAnnotation(),
          updateRelationRequest.getReTag(),updateRelationRequest.getRelation());
      return annotation;
    }
    return "";
  }

  @Override
  public String deleteRelation(DeleteRelationRequest deleteRelationRequest) {
    Optional<AnnotationCombine> optional=annotationCombineRepository.findById(deleteRelationRequest.getId());
    if(optional.isPresent()){
      AnnotationCombine annotationCombine=optional.get();
      String annotation=AnnotationConvert.deleteRelationsAnnotation(annotationCombine.getFinalAnnotation(),
          deleteRelationRequest.getReTag());
      return annotation;
    }
    return "";
  }
}
