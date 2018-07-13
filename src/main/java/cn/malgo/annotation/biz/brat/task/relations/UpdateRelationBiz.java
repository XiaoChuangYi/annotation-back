package cn.malgo.annotation.biz.brat.task.relations;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
@Slf4j
public class UpdateRelationBiz
    extends BaseRelationBiz<UpdateRelationRequest, AnnotationCombineBratVO> {

  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;

  public UpdateRelationBiz(CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService) {
    this.checkLegalRelationBeforeAddService = checkLegalRelationBeforeAddService;
  }

  @Override
  protected void validateRequest(UpdateRelationRequest updateRelationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(updateRelationRequest.getReTag())) {
      throw new InvalidInputException("invalid-reTag", "参数reTag为空");
    }
    if (StringUtils.isBlank(updateRelationRequest.getRelation())) {
      throw new InvalidInputException("invalid-relation", "参数relation为空");
    }
  }

  @Override
  AnnotationCombineBratVO doInternalProcess(
      int role,
      RelationOperateService relationOperateService,
      AnnotationCombine annotationCombine,
      UpdateRelationRequest updateRelationRequest) {
    AnnotationCombineBratVO annotationCombineBratVO;
    if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdate(
        updateRelationRequest, role)) {
      throw new InvalidInputException("illegal-relation-can-not-update", "该关系被关联规则限制，无法更新");
    }
    String annotation = relationOperateService.updateRelation(updateRelationRequest, role);
    if (role > 0 && role < AnnotationRoleStateEnum.labelStaff.getRole()) { // 管理员或者是审核人员级别
      if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.relation.ordinal()) {
        annotationCombine.setReviewedAnnotation(annotation);
      }
    }
    if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) { // 标注人员
      annotationCombine.setFinalAnnotation(annotation);
    }
    annotationCombineBratVO = AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
    return annotationCombineBratVO;
  }
}
