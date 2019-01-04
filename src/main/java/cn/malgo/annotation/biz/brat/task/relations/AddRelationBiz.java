package cn.malgo.annotation.biz.brat.task.relations;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddRelationBiz extends BaseRelationBiz<AddRelationRequest, AnnotationBratVO> {

  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;

  public AddRelationBiz(CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService) {
    this.checkLegalRelationBeforeAddService = checkLegalRelationBeforeAddService;
  }

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
  AnnotationBratVO doInternalProcess(
      RelationOperateService relationOperateService,
      AnnotationNew annotationNew,
      AddRelationRequest addRelationRequest) {
    if (annotationNew.getAnnotationType().ordinal() == AnnotationTypeEnum.relation.ordinal()
        && checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeAdd(
            addRelationRequest)) {
      throw new InvalidInputException("illegal-relation-can-not-add", "该关系被关联规则限制，无法新增");
    }

    relationOperateService.addRelation(annotationNew, addRelationRequest);
    return AnnotationConvert.convert2AnnotationBratVO(annotationNew);
  }
}
