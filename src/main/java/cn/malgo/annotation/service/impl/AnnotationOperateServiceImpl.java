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
      String newAnnotation =
          AnnotationConvert.addEntitiesAnnotation(
              annotationCombine.getManualAnnotation(),
              addAnnotationRequest.getType(),
              addAnnotationRequest.getStartPosition(),
              addAnnotationRequest.getEndPosition(),
              addAnnotationRequest.getTerm());
      annotationCombine.setManualAnnotation(newAnnotation);
      //      annotationCombine.setFinalAnnotation(newAnnotation);
      annotationCombineRepository.save(annotationCombine);
      return newAnnotation;
    }
    return "";
  }
}
