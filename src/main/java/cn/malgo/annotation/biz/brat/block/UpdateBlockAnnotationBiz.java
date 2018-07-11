package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class UpdateBlockAnnotationBiz
    extends BaseBlockAnnotationBiz<UpdateAnnotationGroupRequest, AnnotationBlockBratVO> {

  private final AnnotationWriteOperateService annotationWriteOperateService;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;

  public UpdateBlockAnnotationBiz(
      AnnotationWriteOperateService annotationWriteOperateService,
      AnnotationTaskBlockRepository annotationTaskBlockRepository,
      CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService) {
    this.annotationWriteOperateService = annotationWriteOperateService;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.checkLegalRelationBeforeAddService = checkLegalRelationBeforeAddService;
  }

  @Override
  protected void validateRequest(UpdateAnnotationGroupRequest updateAnnotationGroupRequest)
      throws InvalidInputException {
    if (StringUtils.isAllBlank(
        updateAnnotationGroupRequest.getReTag(), updateAnnotationGroupRequest.getRelation())) {
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
      int role,
      AnnotationTaskBlock annotationTaskBlock,
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest) {
    checkRuleBeforeUpdateRelation(updateAnnotationGroupRequest, annotationTaskBlock, role);
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
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock,
      int role) {
    if (!StringUtils.isAllBlank(
        updateAnnotationGroupRequest.getRelation(), updateAnnotationGroupRequest.getReTag())) {
      // 新增relation
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeUpdate(
          updateAnnotationGroupRequest, annotationTaskBlock, role)) {
        throw new InvalidInputException("illegal-relation-can-not-update", "该关系被关联规则限制，无法更新");
      }
    }
  }
}
