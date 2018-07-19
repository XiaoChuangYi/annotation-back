package cn.malgo.annotation.biz.brat.task.relations;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

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
      RelationOperateService relationOperateService,
      AnnotationCombine annotationCombine,
      UpdateRelationRequest request) {
    if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdate(request)) {
      throw new InvalidInputException("illegal-relation-can-not-update", "该关系被关联规则限制，无法更新");
    }

    relationOperateService.updateRelation(annotationCombine, request);
    return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
  }
}
