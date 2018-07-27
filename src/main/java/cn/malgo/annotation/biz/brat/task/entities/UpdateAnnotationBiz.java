package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateAnnotationBiz
    extends BaseAnnotationBiz<UpdateAnnotationRequest, AnnotationCombineBratVO> {

  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;
  private final CheckRelationEntityService checkRelationEntityService;

  public UpdateAnnotationBiz(
      final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService,
      final CheckRelationEntityService checkRelationEntityService) {
    this.checkLegalRelationBeforeAddService = checkLegalRelationBeforeAddService;
    this.checkRelationEntityService = checkRelationEntityService;
  }

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
  AnnotationCombineBratVO doInternalProcess(
      AnnotationOperateService annotationOperateService,
      AnnotationCombine annotationCombine,
      UpdateAnnotationRequest updateAnnotationRequest) {
    if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.relation.ordinal()) {
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdateEntity(
          updateAnnotationRequest)) {
        throw new InvalidInputException("illegal-relation-can-not-update", "该关系被关联规则限制，无法更新");
      }
      if (checkRelationEntityService.checkRelationEntityBeforeUpdate(
          updateAnnotationRequest, getAnnotation(annotationCombine))) {
        throw new InvalidInputException("in-conformity-association-rules", "不符合关联规则，无法更新");
      }
    }
    annotationOperateService.updateAnnotation(annotationCombine, updateAnnotationRequest);
    return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
  }
}
