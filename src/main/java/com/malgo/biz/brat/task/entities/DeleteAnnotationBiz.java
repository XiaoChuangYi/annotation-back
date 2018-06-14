package com.malgo.biz.brat.task.entities;

import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationRoleStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
@Slf4j
public class DeleteAnnotationBiz
    extends BaseAnnotationBiz<DeleteAnnotationRequest, AnnotationCombineBratVO> {

  @Override
  protected void validateRequest(DeleteAnnotationRequest deleteAnnotationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(deleteAnnotationRequest.getTag())) {
      throw new InvalidInputException("invalid-tag", "参数tag为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, DeleteAnnotationRequest deleteAnnotationRequest)
      throws BusinessRuleException {}

  @Override
  AnnotationCombineBratVO doInternalProcess(
      int role,
      AnnotationOperateService annotationOperateService,
      AnnotationCombine annotationCombine,
      DeleteAnnotationRequest deleteAnnotationRequest) {
    AnnotationCombineBratVO annotationCombineBratVO;
    String annotation = annotationOperateService.deleteAnnotation(deleteAnnotationRequest);
    if (role > 0 && role < AnnotationRoleStateEnum.labelStaff.getRole()) {
      // 管理员，审核人员
      annotationCombine.setReviewedAnnotation(annotation);
    }
    if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) {
      // 标注人员，练习人员
      annotationCombine.setFinalAnnotation(annotation);
    }
    annotationCombineBratVO = AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
    return annotationCombineBratVO;
  }
}
