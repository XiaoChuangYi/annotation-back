package com.malgo.biz.brat.task.entities;

import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationRoleStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
@Slf4j
public class UpdateAnnotationBiz
    extends BaseAnnotationBiz<UpdateAnnotationRequest, AnnotationCombineBratVO> {

  @Override
  protected void validateRequest(UpdateAnnotationRequest updateAnnotationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(updateAnnotationRequest.getTag())) {
      throw new InvalidInputException("invalid-tag", "参数tag不能为空");
    }
    if (StringUtils.isBlank(updateAnnotationRequest.getNewType())) {
      throw new InvalidInputException("invalid-new-type", "参数newType不能为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, UpdateAnnotationRequest updateAnnotationRequest)
      throws BusinessRuleException {}

  @Override
  AnnotationCombineBratVO doInternalProcess(
      int role,
      AnnotationOperateService annotationOperateService,
      AnnotationCombine annotationCombine,
      UpdateAnnotationRequest updateAnnotationRequest) {
    AnnotationCombineBratVO annotationCombineBratVO;
    String annotation = annotationOperateService.updateAnnotation(updateAnnotationRequest);
    if (role > 0 && role < AnnotationRoleStateEnum.labelStaff.getRole()) {
      // 管理员或者是审核人员级别
      annotationCombine.setReviewedAnnotation(annotation);
    }
    if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) {
      // 标注人员
      annotationCombine.setFinalAnnotation(annotation);
    }
    annotationCombineBratVO = AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
    return annotationCombineBratVO;
  }
}
