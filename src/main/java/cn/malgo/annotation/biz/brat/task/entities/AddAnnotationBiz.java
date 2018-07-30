package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddAnnotationBiz
    extends BaseAnnotationBiz<AddAnnotationRequest, AnnotationCombineBratVO> {

  private final CheckRelationEntityService checkRelationEntityService;

  public AddAnnotationBiz(final CheckRelationEntityService checkRelationEntityService) {
    this.checkRelationEntityService = checkRelationEntityService;
  }

  @Override
  protected void validateRequest(AddAnnotationRequest addAnnotationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(addAnnotationRequest.getTerm())) {
      throw new InvalidInputException("invalid-term", "term参数为空");
    }

    if (StringUtils.isBlank(addAnnotationRequest.getType())) {
      throw new InvalidInputException("invalid-annotation-type", "type参数为空");
    }

    if (addAnnotationRequest.getStartPosition() < 0) {
      throw new InvalidInputException("invalid-start-position", "无效的startPosition");
    }

    if (addAnnotationRequest.getEndPosition() <= 0) {
      throw new InvalidInputException("invalid-end-position", "无效的endPosition");
    }
  }

  @Override
  AnnotationCombineBratVO doInternalProcess(
      AnnotationOperateService annotationOperateService,
      AnnotationCombine annotationCombine,
      AddAnnotationRequest addAnnotationRequest) {
    if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.relation.ordinal()) {
      if (checkRelationEntityService.checkRelationEntityBeforeAdd(
          addAnnotationRequest, getAnnotation(annotationCombine))) {
        throw new BusinessRuleException("in-conformity-association-rules", "不符合关联规则，无法新增");
      }
      if (checkRelationEntityService.addRelationEntityCheckAnchorSide(
          addAnnotationRequest, getAnnotation(annotationCombine))) {
        throw new BusinessRuleException(
            "in-conformity-association-rules-anchor", "不符合关联规则，锚点前实体类型重复，无法新增");
      }
    }
    annotationOperateService.addAnnotation(annotationCombine, addAnnotationRequest);
    return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
  }
}
