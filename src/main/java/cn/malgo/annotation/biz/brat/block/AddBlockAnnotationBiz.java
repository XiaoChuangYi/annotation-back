package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
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
public class AddBlockAnnotationBiz
    extends BaseBlockAnnotationBiz<AddAnnotationGroupRequest, AnnotationBlockBratVO> {

  private final AnnotationWriteOperateService annotationWriteOperateService;
  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;
  private final CheckRelationEntityService checkRelationEntityService;

  public AddBlockAnnotationBiz(
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
  protected void validateRequest(AddAnnotationGroupRequest addAnnotationGroupRequest)
      throws InvalidInputException {
    if (addAnnotationGroupRequest.isAddEntity()) {
      if (StringUtils.isBlank(addAnnotationGroupRequest.getTerm())) {
        throw new InvalidInputException("invalid-term", "term????????????");
      }

      if (StringUtils.isBlank(addAnnotationGroupRequest.getType())) {
        throw new InvalidInputException("invalid-annotation-type", "type????????????");
      }

      if (addAnnotationGroupRequest.getStartPosition() < 0) {
        throw new InvalidInputException("invalid-start-position", "?????????startPosition");
      }

      if (addAnnotationGroupRequest.getEndPosition() <= 0) {
        throw new InvalidInputException("invalid-end-position", "?????????endPosition");
      }
    } else {
      if (StringUtils.isBlank(addAnnotationGroupRequest.getSourceTag())) {
        throw new InvalidInputException("invalid-source-tag", "??????sourceTag??????");
      }

      if (StringUtils.isBlank(addAnnotationGroupRequest.getTargetTag())) {
        throw new InvalidInputException("invalid-target-tag", "??????targetTag??????");
      }

      if (StringUtils.isBlank(addAnnotationGroupRequest.getRelation())) {
        throw new InvalidInputException("invalid-relation", "??????relation??????");
      }
    }
  }

  @Override
  AnnotationBlockBratVO doInternalProcess(
      AnnotationTaskBlock annotationTaskBlock, AddAnnotationGroupRequest request) {
    if (annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.relation
        || annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.medicine_books) {
      checkRuleBeforeAddRelation(request, annotationTaskBlock);
    }
    final String annotation =
        annotationWriteOperateService.addMetaDataAnnotation(
            request,
            annotationTaskBlock.getAnnotation(),
            annotationTaskBlock.getAnnotationType().ordinal());
    return AnnotationConvert.convert2AnnotationBlockBratVO(
        saveAnnotation(annotationTaskBlock, annotation));
  }

  private void checkRuleBeforeAddRelation(
      AddAnnotationGroupRequest request, AnnotationTaskBlock annotationTaskBlock) {
    if (!request.isAddEntity()) {
      // ??????relation
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeAdd(
          request, annotationTaskBlock)) {
        throw new InvalidInputException("illegal-relation-can-not-add", "?????????????????????????????????????????????");
      }
    } else {
      final AddAnnotationRequest addReq =
          new AddAnnotationRequest(
              request.getId(),
              request.getTerm(),
              request.getType(),
              request.getStartPosition(),
              request.getEndPosition(),
              "");
      final Annotation annotation = getAnnotation(annotationTaskBlock);
      if (checkRelationEntityService.checkRelationEntityBeforeAdd(addReq, annotation)) {
        throw new BusinessRuleException("in-conformity-association-rules", "????????????????????????????????????");
      }
      if (checkRelationEntityService.addRelationEntityCheckAnchorSide(addReq, annotation)) {
        throw new BusinessRuleException(
            "in-conformity-association-rules-anchor", "??????????????????????????????????????????????????????????????????");
      }
    }
  }
}
