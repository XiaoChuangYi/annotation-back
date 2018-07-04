package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.utils.AnnotationConvert;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/5/31. */
@Service("local")
@Slf4j
public class AnnotationOperateServiceImpl extends BaseAnnotationOperateImpl {

  public AnnotationOperateServiceImpl(AnnotationCombineRepository annotationCombineRepository) {
    super(annotationCombineRepository);
  }

  @Override
  public String addAnnotation(
      AnnotationCombineRepository annotationCombineRepository,
      AddAnnotationRequest addAnnotationRequest,
      int roleId) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation.name())) {
        annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      }
      String newAnnotation = "";
      if (roleId > AnnotationRoleStateEnum.auditor.getRole()) {
        newAnnotation =
            AnnotationConvert.addEntitiesAnnotation(
                annotationCombine.getFinalAnnotation(),
                addAnnotationRequest.getType(),
                addAnnotationRequest.getStartPosition(),
                addAnnotationRequest.getEndPosition(),
                addAnnotationRequest.getTerm());
        annotationCombine.setFinalAnnotation(newAnnotation);
      } else if (roleId > 0 && roleId <= AnnotationRoleStateEnum.auditor.getRole()) {
        newAnnotation =
            AnnotationConvert.addEntitiesAnnotation(
                annotationCombine.getReviewedAnnotation(),
                addAnnotationRequest.getType(),
                addAnnotationRequest.getStartPosition(),
                addAnnotationRequest.getEndPosition(),
                addAnnotationRequest.getTerm());
        annotationCombine.setReviewedAnnotation(newAnnotation);
      }
      annotationCombineRepository.save(annotationCombine);
      return newAnnotation;
    }
    return "";
  }
}
