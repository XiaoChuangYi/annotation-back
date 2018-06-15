package com.malgo.biz.brat.task.relations;

import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationRoleStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.DeleteRelationRequest;
import com.malgo.service.RelationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
@Slf4j
public class DeleteRelationBiz
    extends BaseRelationBiz<DeleteRelationRequest, AnnotationCombineBratVO> {

  @Override
  protected void validateRequest(DeleteRelationRequest deleteRelationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(deleteRelationRequest.getReTag())) {
      throw new InvalidInputException("invalid-reTag", "参数reTag为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, DeleteRelationRequest deleteRelationRequest)
      throws BusinessRuleException {}

  @Override
  AnnotationCombineBratVO doInternalProcess(
      int role,
      RelationOperateService relationOperateService,
      AnnotationCombine annotationCombine,
      DeleteRelationRequest deleteRelationRequest) {
    AnnotationCombineBratVO annotationCombineBratVO;
    String annotation = relationOperateService.deleteRelation(deleteRelationRequest);
    if (role > 0 && role < AnnotationRoleStateEnum.labelStaff.getRole()) { // 管理员或者是审核人员级别
      annotationCombine.setReviewedAnnotation(annotation);
    }
    if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) { // 标注人员
      annotation = relationOperateService.deleteRelation(deleteRelationRequest);
      annotationCombine.setFinalAnnotation(annotation);
    }
    annotationCombineBratVO = AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
    return annotationCombineBratVO;
  }
}
