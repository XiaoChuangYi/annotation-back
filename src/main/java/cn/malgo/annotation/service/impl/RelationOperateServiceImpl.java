package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
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
  public String addRelation(AddRelationRequest addRelationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String annotation =
          AnnotationConvert.addRelationsAnnotation(
              annotationCombine.getManualAnnotation(),
              addRelationRequest.getSourceTag(),
              addRelationRequest.getTargetTag(),
              addRelationRequest.getRelation());
      annotationCombine.setManualAnnotation(annotation);
      annotationCombineRepository.save(annotationCombine);
      return annotation;
    }
    return "";
  }

  @Override
  public String updateRelation(UpdateRelationRequest updateRelationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(updateRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String annotation =
          AnnotationConvert.updateRelationAnnotation(
              annotationCombine.getManualAnnotation(),
              updateRelationRequest.getReTag(),
              updateRelationRequest.getRelation());
      annotationCombine.setManualAnnotation(annotation);
      annotationCombineRepository.save(annotationCombine);
      return annotation;
    }
    return "";
  }

  @Override
  public String deleteRelation(DeleteRelationRequest deleteRelationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(deleteRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String annotation =
          AnnotationConvert.deleteRelationsAnnotation(
              annotationCombine.getManualAnnotation(), deleteRelationRequest.getReTag());
      annotationCombine.setManualAnnotation(annotation);
      annotationCombineRepository.save(annotationCombine);
      return annotation;
    }
    return "";
  }
}
