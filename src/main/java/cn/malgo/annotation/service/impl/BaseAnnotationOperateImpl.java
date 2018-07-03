package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

/** Created by cjl on 2018/6/14. */
public abstract class BaseAnnotationOperateImpl implements AnnotationOperateService {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public BaseAnnotationOperateImpl(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  public abstract String addAnnotation(
      AnnotationCombineRepository annotationCombineRepository,
      AddAnnotationRequest addAnnotationRequest,
      int roleId);

  @Override
  public String addAnnotation(AddAnnotationRequest addAnnotationRequest, int roleId) {
    return addAnnotation(annotationCombineRepository, addAnnotationRequest, roleId);
  }

  @Override
  public String deleteAnnotation(DeleteAnnotationRequest deleteAnnotationRequest, int roleId) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(deleteAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      String annotation =
          AnnotationConvert.deleteEntitiesAnnotation(
              annotationCombine.getManualAnnotation(), deleteAnnotationRequest.getTag());
      //      if (roleId >= AnnotationRoleStateEnum.labelStaff.getRole()) {
      //      } else {
      //        annotationCombine.setReviewedAnnotation(annotation);
      //      }
      annotationCombine.setManualAnnotation(annotation);
      annotationCombineRepository.save(annotationCombine);
      return annotation;
    }
    return "";
  }

  @Override
  public String updateAnnotation(UpdateAnnotationRequest updateAnnotationRequest, int roleId) {
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
