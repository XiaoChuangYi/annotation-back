package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateAnnotationBiz
    extends BaseAnnotationBiz<UpdateAnnotationGroupRequest, AnnotationBratVO> {

  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;
  private final CheckRelationEntityService checkRelationEntityService;
  private final AnnotationWriteOperateService annotationWriteOperateService;
  private final AnnotationRepository annotationRepository;

  public UpdateAnnotationBiz(
      final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService,
      final CheckRelationEntityService checkRelationEntityService,
      final AnnotationWriteOperateService annotationWriteOperateService,
      final AnnotationRepository annotationRepository) {
    this.checkLegalRelationBeforeAddService = checkLegalRelationBeforeAddService;
    this.checkRelationEntityService = checkRelationEntityService;
    this.annotationWriteOperateService = annotationWriteOperateService;
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(UpdateAnnotationGroupRequest request)
      throws InvalidInputException {
    if (StringUtils.isBlank(request.getTag())) {
      throw new InvalidInputException("invalid-tag", "??????tag????????????");
    }
    if (StringUtils.isBlank(request.getNewType())) {
      throw new InvalidInputException("invalid-new-type", "??????newType????????????");
    }
  }

  @Override
  AnnotationBratVO doInternalProcess(
      AnnotationNew annotationNew, UpdateAnnotationGroupRequest request) {
    final UpdateAnnotationRequest paramRequest = new UpdateAnnotationRequest();
    BeanUtils.copyProperties(request, paramRequest);
    if (annotationNew.getAnnotationType() == AnnotationTypeEnum.relation) {
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdateEntity(
          paramRequest)) { // ?????????????????????
        throw new InvalidInputException("illegal-relation-can-not-update", "?????????????????????????????????????????????");
      }
      if (checkRelationEntityService.checkRelationEntityBeforeUpdate(
          paramRequest, getAnnotation(annotationNew))) {
        throw new BusinessRuleException(
            "in-conformity-association-rules-text-cross", "???????????????????????????????????????????????????");
      }
      if (checkRelationEntityService.updateRelationEntityCheckAnchorSide(
          paramRequest, getAnnotation(annotationNew))) {
        throw new BusinessRuleException(
            "in-conformity-association-rules-text-cross", "????????????????????????????????????????????????????????????????????????");
      }
    }
    final String annotation =
        annotationWriteOperateService.updateMetaDataAnnotation(
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
