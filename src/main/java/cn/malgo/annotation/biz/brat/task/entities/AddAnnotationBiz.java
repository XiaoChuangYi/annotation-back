package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AddAnnotationBiz
    extends BaseAnnotationBiz<AddAnnotationGroupRequest, AnnotationBratVO> {

  private final CheckRelationEntityService checkRelationEntityService;
  private final AnnotationWriteOperateService annotationWriteOperateService;
  private final AnnotationRepository annotationRepository;

  public AddAnnotationBiz(
      final CheckRelationEntityService checkRelationEntityService,
      final AnnotationWriteOperateService annotationWriteOperateService,
      final AnnotationRepository annotationRepository) {
    this.checkRelationEntityService = checkRelationEntityService;
    this.annotationWriteOperateService = annotationWriteOperateService;
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(AddAnnotationGroupRequest request) throws InvalidInputException {
    if (!request.isAddEntity()) {
      if (StringUtils.isBlank(request.getSourceTag())) {
        throw new InvalidInputException("invalid-source-tag", "参数sourceTag为空");
      }

      if (StringUtils.isBlank(request.getTargetTag())) {
        throw new InvalidInputException("invalid-target-tag", "参数targetTag为空");
      }

      if (StringUtils.isBlank(request.getRelation())) {
        throw new InvalidInputException("invalid-relation", "参数relation为空");
      }
    } else {
      if (StringUtils.isBlank(request.getTerm())) {
        throw new InvalidInputException("invalid-term", "term参数为空");
      }

      if (StringUtils.isBlank(request.getType())) {
        throw new InvalidInputException("invalid-annotation-type", "type参数为空");
      }

      if (request.getStartPosition() < 0) {
        throw new InvalidInputException("invalid-start-position", "无效的startPosition");
      }

      if (request.getEndPosition() <= 0) {
        throw new InvalidInputException("invalid-end-position", "无效的endPosition");
      }
    }
  }

  @Override
  AnnotationBratVO doInternalProcess(
      AnnotationNew annotationNew, AddAnnotationGroupRequest request) {
    final AddAnnotationRequest paramRequest = new AddAnnotationRequest();
    BeanUtils.copyProperties(request, paramRequest);
    if (annotationNew.getAnnotationType() == AnnotationTypeEnum.relation) {
      if (checkRelationEntityService.checkRelationEntityBeforeAdd(
          paramRequest, getAnnotation(annotationNew))) {
        throw new BusinessRuleException("in-conformity-association-rules", "不符合关联规则，无法新增");
      }
      if (checkRelationEntityService.addRelationEntityCheckAnchorSide(
          paramRequest, getAnnotation(annotationNew))) {
        throw new BusinessRuleException(
            "in-conformity-association-rules-anchor", "不符合关联规则，锚点前实体类型重复，无法新增");
      }
    }
    final String annotation =
        annotationWriteOperateService.addMetaDataAnnotation(
            request,
            annotationNew.getFinalAnnotation(),
            annotationNew.getAnnotationType().ordinal());
    if (annotationNew.getState() == AnnotationStateEnum.PRE_ANNOTATION) {
      annotationNew.setState(AnnotationStateEnum.ANNOTATION_PROCESSING);
    }
    annotationNew.setFinalAnnotation(annotation);
    annotationRepository.save(annotationNew);
    return AnnotationConvert.convert2AnnotationBratVO(annotationNew);
  }
}
