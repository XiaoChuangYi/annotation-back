package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/5/31. */
@Service("task-relation")
public class RelationOperateServiceImpl implements RelationOperateService {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public RelationOperateServiceImpl(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  public String addRelation(AddRelationRequest addRelationRequest, int roleId) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
        annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      }
      String annotation;
      if (roleId >= AnnotationRoleStateEnum.labelStaff.getRole()) {
        annotation =
            AnnotationConvert.addRelationsAnnotation(
                annotationCombine.getFinalAnnotation(),
                addRelationRequest.getSourceTag(),
                addRelationRequest.getTargetTag(),
                addRelationRequest.getRelation());
        annotationCombine.setFinalAnnotation(annotation);
      } else {
        annotation =
            AnnotationConvert.addRelationsAnnotation(
                annotationCombine.getReviewedAnnotation(),
                addRelationRequest.getSourceTag(),
                addRelationRequest.getTargetTag(),
                addRelationRequest.getRelation());
        annotationCombine.setReviewedAnnotation(annotation);
      }
      annotationCombineRepository.save(annotationCombine);
      return annotation;
    }
    return "";
  }

  @Override
  public String updateRelation(UpdateRelationRequest updateRelationRequest, int roleId) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(updateRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
        annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      }
      String annotation;
      if (roleId >= AnnotationRoleStateEnum.labelStaff.getRole()) {
        annotation =
            AnnotationConvert.updateRelationAnnotation(
                annotationCombine.getFinalAnnotation(),
                updateRelationRequest.getReTag(),
                updateRelationRequest.getRelation());
        annotationCombine.setFinalAnnotation(annotation);
      } else {
        annotation =
            AnnotationConvert.updateRelationAnnotation(
                annotationCombine.getReviewedAnnotation(),
                updateRelationRequest.getReTag(),
                updateRelationRequest.getRelation());
        annotationCombine.setReviewedAnnotation(annotation);
      }
      annotationCombineRepository.save(annotationCombine);
      return annotation;
    }
    return "";
  }

  @Override
  public String deleteRelation(DeleteRelationRequest deleteRelationRequest, int roleId) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(deleteRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
        annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      }
      String annotation;
      if (roleId >= AnnotationRoleStateEnum.labelStaff.getRole()) {
        annotation =
            AnnotationConvert.deleteRelationsAnnotation(
                annotationCombine.getFinalAnnotation(), deleteRelationRequest.getReTag());
        annotationCombine.setFinalAnnotation(annotation);
      } else {
        annotation =
            AnnotationConvert.deleteRelationsAnnotation(
                annotationCombine.getReviewedAnnotation(), deleteRelationRequest.getReTag());
        annotationCombine.setReviewedAnnotation(annotation);
      }
      annotationCombineRepository.save(annotationCombine);
      return annotation;
    }
    return "";
  }
}
