package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.service.AnnotationExamineService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.service.exception.BusinessRuleException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnotationExamineServiceImpl implements AnnotationExamineService {

  @Override
  public List<Long> batchAnnotationExamine(List<AnnotationCombine> annotationCombines) {
    final List<Long> forbidList = new ArrayList<>();
    final List<AnnotationCombine> combineList =
        annotationCombines
            .stream()
            .map(
                annotationCombine -> {
                  if (handleCurrentAnnotation(annotationCombine).intValue() > 0) {
                    log.info(
                        "id为{},状态为{}的标注无法被审核提交",
                        annotationCombine.getId(),
                        annotationCombine.getState());
                    forbidList.add(annotationCombine.getId());
                  }
                  return annotationCombine;
                })
            .collect(Collectors.toList());
    return forbidList;
  }

  @Override
  public Long singleAnnotationExamine(AnnotationCombine annotationCombine) {
    return handleCurrentAnnotation(annotationCombine);
  }

  private Long handleCurrentAnnotation(AnnotationCombine annotationCombine) {
    if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preExamine.name())) {
      final boolean equals =
          AnnotationConvert.compareAnnotation(
              annotationCombine.getFinalAnnotation(), annotationCombine.getReviewedAnnotation());
      if (equals) {
        annotationCombine.setState(AnnotationCombineStateEnum.examinePass.name());
      } else {
        annotationCombine.setState(AnnotationCombineStateEnum.errorPass.name());
      }
    } else if (annotationCombine.getState().equals(AnnotationCombineStateEnum.abandon.name())) {
      annotationCombine.setState(AnnotationCombineStateEnum.innerAnnotation.name());
    } else {
      return annotationCombine.getId();
    }
    return Long.valueOf(0);
  }
}
