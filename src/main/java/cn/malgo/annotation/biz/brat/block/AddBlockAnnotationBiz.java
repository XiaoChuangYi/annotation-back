package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class AddBlockAnnotationBiz
    extends BaseBlockAnnotationBiz<AddAnnotationGroupRequest, AnnotationBlockBratVO> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationWriteOperateService annotationWriteOperateService;
  private final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService;

  public AddBlockAnnotationBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationWriteOperateService annotationWriteOperateService,
      final CheckLegalRelationBeforeAddService checkLegalRelationBeforeAddService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationWriteOperateService = annotationWriteOperateService;
    this.checkLegalRelationBeforeAddService = checkLegalRelationBeforeAddService;
  }

  @Override
  protected void validateRequest(AddAnnotationGroupRequest addAnnotationGroupRequest)
      throws InvalidInputException {
    if (addAnnotationGroupRequest.isAddEntity()) {
      if (StringUtils.isBlank(addAnnotationGroupRequest.getTerm())) {
        throw new InvalidInputException("invalid-term", "term参数为空");
      }

      if (StringUtils.isBlank(addAnnotationGroupRequest.getType())) {
        throw new InvalidInputException("invalid-annotation-type", "type参数为空");
      }

      if (addAnnotationGroupRequest.getStartPosition() < 0) {
        throw new InvalidInputException("invalid-start-position", "无效的startPosition");
      }

      if (addAnnotationGroupRequest.getEndPosition() <= 0) {
        throw new InvalidInputException("invalid-end-position", "无效的endPosition");
      }
    } else {
      if (StringUtils.isBlank(addAnnotationGroupRequest.getSourceTag())) {
        throw new InvalidInputException("invalid-source-tag", "参数sourceTag为空");
      }

      if (StringUtils.isBlank(addAnnotationGroupRequest.getTargetTag())) {
        throw new InvalidInputException("invalid-target-tag", "参数targetTag为空");
      }

      if (StringUtils.isBlank(addAnnotationGroupRequest.getRelation())) {
        throw new InvalidInputException("invalid-relation", "参数relation为空");
      }
    }
  }

  @Override
  AnnotationBlockBratVO doInternalProcess(
      int role, AnnotationTaskBlock annotationTaskBlock, AddAnnotationGroupRequest request) {
    checkRuleBeforeAddRelation(request, annotationTaskBlock);
    final String annotation =
        annotationWriteOperateService.addMetaDataAnnotation(
            request,
            annotationTaskBlock.getAnnotation(),
            annotationTaskBlock.getAnnotationType().ordinal());
    annotationTaskBlock.setAnnotation(annotation);
    annotationTaskBlockRepository.save(annotationTaskBlock);
    return AnnotationConvert.convert2AnnotationBlockBratVO(annotationTaskBlock);
  }

  private void checkRuleBeforeAddRelation(
      AddAnnotationGroupRequest request, AnnotationTaskBlock annotationTaskBlock) {
    if (!request.isAddEntity()) {
      // 新增relation
      if (checkLegalRelationBeforeAddService.checkRelationIsNotLegalBeforeAdd(
          request, annotationTaskBlock)) {
        throw new InvalidInputException("illegal-relation-can-not-add", "该关系被关联规则限制，无法新增");
      }
    }
  }
}
