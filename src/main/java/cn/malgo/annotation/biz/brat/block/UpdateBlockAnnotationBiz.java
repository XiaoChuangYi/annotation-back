package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class UpdateBlockAnnotationBiz
    extends BaseBlockAnnotationBiz<UpdateAnnotationGroupRequest, AnnotationBlockBratVO> {

  private final AnnotationWriteOperateService annotationWriteOperateService;
  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;
  private final CheckRelationEntityService checkRelationEntityService;

  public UpdateBlockAnnotationBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationFactory annotationFactory,
      final ExtractAddAtomicTermService extractAddAtomicTermService,
      final AnnotationWriteOperateService annotationWriteOperateService,
      final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService,
      final CheckRelationEntityService checkRelationEntityService) {
    super(annotationTaskBlockRepository, annotationFactory, extractAddAtomicTermService);

    this.annotationWriteOperateService = annotationWriteOperateService;
    this.checkLegalRelationBeforeAddService = checkLegalRelationBeforeAddService;
    this.checkRelationEntityService = checkRelationEntityService;
  }

  @Override
  protected void validateRequest(UpdateAnnotationGroupRequest updateAnnotationGroupRequest)
      throws InvalidInputException {
    if (updateAnnotationGroupRequest.isUpdatingEntity()) {
      // ??????entity??????
      if (StringUtils.isBlank(updateAnnotationGroupRequest.getTag())) {
        throw new InvalidInputException("invalid-tag", "??????tag????????????");
      }

      if (StringUtils.isBlank(updateAnnotationGroupRequest.getNewType())) {
        throw new InvalidInputException("invalid-new-type", "??????newType????????????");
      }
    } else {
      if (StringUtils.isBlank(updateAnnotationGroupRequest.getReTag())) {
        throw new InvalidInputException("invalid-reTag", "??????reTag??????");
      }

      if (StringUtils.isBlank(updateAnnotationGroupRequest.getRelation())) {
        throw new InvalidInputException("invalid-relation", "??????relation??????");
      }
    }
  }

  @Override
  AnnotationBlockBratVO doInternalProcess(
      AnnotationTaskBlock annotationTaskBlock,
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest) {
    if (annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.relation
        || annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.medicine_books) {
      checkRuleBeforeUpdateRelation(updateAnnotationGroupRequest, annotationTaskBlock);
    }
    final String annotation =
        annotationWriteOperateService.updateMetaDataAnnotation(
            updateAnnotationGroupRequest,
            annotationTaskBlock.getAnnotation(),
            annotationTaskBlock.getAnnotationType().ordinal());
    return AnnotationConvert.convert2AnnotationBlockBratVO(
        saveAnnotation(annotationTaskBlock, annotation));
  }

  private void checkRuleBeforeUpdateRelation(
      UpdateAnnotationGroupRequest request, AnnotationTaskBlock annotationTaskBlock) {
    if (request.isUpdatingEntity()) {
      // ??????entity
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdateEntity(
          request, annotationTaskBlock)) {
        throw new InvalidInputException("illegal-relation-can-not-update", "?????????????????????????????????????????????");
      }
      final Annotation annotation = getAnnotation(annotationTaskBlock);
      final UpdateAnnotationRequest updateReq =
          new UpdateAnnotationRequest(
              request.getId(),
              request.getTag(),
              request.getNewType(),
              "",
              request.getStartPosition(),
              request.getEndPosition(),
              request.getTerm());

      if (checkRelationEntityService.checkRelationEntityBeforeUpdate(updateReq, annotation)) {
        throw new BusinessRuleException(
            "in-conformity-association-rules-text-cross", "???????????????????????????????????????????????????");
      }
      if (checkRelationEntityService.updateRelationEntityCheckAnchorSide(updateReq, annotation)) {
        throw new BusinessRuleException(
            "in-conformity-association-rules-text-cross", "????????????????????????????????????????????????????????????????????????");
      }
    } else {
      // ??????relation
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdate(
          request, annotationTaskBlock)) {
        throw new InvalidInputException("illegal-relation-can-not-update", "?????????????????????????????????????????????");
      }
    }
  }
}
