package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class UpdateBlockAnnotationBiz
    extends BaseBlockAnnotationBiz<UpdateAnnotationGroupRequest, AnnotationBlockBratVO> {

  private final AnnotationWriteOperateService annotationWriteOperateService;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;
  private final CheckRelationEntityService checkRelationEntityService;

  public UpdateBlockAnnotationBiz(
      AnnotationWriteOperateService annotationWriteOperateService,
      AnnotationTaskBlockRepository annotationTaskBlockRepository,
      CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService,
      CheckRelationEntityService checkRelationEntityService) {
    this.annotationWriteOperateService = annotationWriteOperateService;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.checkLegalRelationBeforeAddService = checkLegalRelationBeforeAddService;
    this.checkRelationEntityService = checkRelationEntityService;
  }

  @Override
  protected void validateRequest(UpdateAnnotationGroupRequest updateAnnotationGroupRequest)
      throws InvalidInputException {
    if (updateAnnotationGroupRequest.isUpdatingEntity()) {
      // 更新entity校验
      if (StringUtils.isBlank(updateAnnotationGroupRequest.getTag())) {
        throw new InvalidInputException("invalid-tag", "参数tag不能为空");
      }

      if (StringUtils.isBlank(updateAnnotationGroupRequest.getNewType())) {
        throw new InvalidInputException("invalid-new-type", "参数newType不能为空");
      }
    } else {
      if (StringUtils.isBlank(updateAnnotationGroupRequest.getReTag())) {
        throw new InvalidInputException("invalid-reTag", "参数reTag为空");
      }

      if (StringUtils.isBlank(updateAnnotationGroupRequest.getRelation())) {
        throw new InvalidInputException("invalid-relation", "参数relation为空");
      }
    }
  }

  @Override
  AnnotationBlockBratVO doInternalProcess(
      AnnotationTaskBlock annotationTaskBlock,
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest) {
    if (annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.relation) {
      checkRuleBeforeUpdateRelation(updateAnnotationGroupRequest, annotationTaskBlock);
    }
    final String annotation =
        annotationWriteOperateService.updateMetaDataAnnotation(
            updateAnnotationGroupRequest,
            annotationTaskBlock.getAnnotation(),
            annotationTaskBlock.getAnnotationType().ordinal());
    annotationTaskBlock.setAnnotation(annotation);
    annotationTaskBlockRepository.save(annotationTaskBlock);
    return AnnotationConvert.convert2AnnotationBlockBratVO(annotationTaskBlock);
  }

  private void checkRuleBeforeUpdateRelation(
      UpdateAnnotationGroupRequest request, AnnotationTaskBlock annotationTaskBlock) {
    if (request.isUpdatingEntity()) {
      // 更新entity
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdateEntity(
          request, annotationTaskBlock)) {
        throw new InvalidInputException("illegal-relation-can-not-update", "该关系被关联规则限制，无法更新");
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
            "in-conformity-association-rules-text-cross", "不符合关联规则，文本交叉，无法更新");
      }
      if (checkRelationEntityService.updateRelationEntityCheckAnchorSide(updateReq, annotation)) {
        throw new BusinessRuleException(
            "in-conformity-association-rules-text-cross", "不符合关联规则，锚点前两个实体类型重复，无法更新");
      }
    } else {
      // 更新relation
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdate(
          request, annotationTaskBlock)) {
        throw new InvalidInputException("illegal-relation-can-not-update", "该关系被关联规则限制，无法更新");
      }
    }
  }
}
