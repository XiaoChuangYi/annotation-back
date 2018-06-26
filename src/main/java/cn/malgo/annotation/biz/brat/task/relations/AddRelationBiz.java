package cn.malgo.annotation.biz.brat.task.relations;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
@Slf4j
public class AddRelationBiz extends BaseRelationBiz<AddRelationRequest, AnnotationCombineBratVO> {

  @Override
  protected void validateRequest(AddRelationRequest addRelationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(addRelationRequest.getSourceTag())) {
      throw new InvalidInputException("invalid-source-tag", "参数sourceTag为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getTargetTag())) {
      throw new InvalidInputException("invalid-target-tag", "参数targetTag为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getRelation())) {
      throw new InvalidInputException("invalid-relation", "参数relation为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, AddRelationRequest addRelationRequest)
      throws BusinessRuleException {}

  @Override
  AnnotationCombineBratVO doInternalProcess(
      int role,
      RelationOperateService relationOperateService,
      AnnotationCombine annotationCombine,
      AddRelationRequest addRelationRequest) {
    AnnotationCombineBratVO annotationCombineBratVO;
    String annotation = relationOperateService.addRelation(addRelationRequest);
    if (role > 0 && role < AnnotationRoleStateEnum.labelStaff.getRole()) { // 管理员或者是审核人员级别
      annotationCombine.setReviewedAnnotation(annotation);
    }
    if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) { // 标注人员
      annotationCombine.setFinalAnnotation(annotation);
    }
    annotationCombineBratVO = AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
    return annotationCombineBratVO;
  }
}
