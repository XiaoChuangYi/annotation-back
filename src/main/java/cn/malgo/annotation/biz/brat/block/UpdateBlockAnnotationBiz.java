package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
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

  public UpdateBlockAnnotationBiz(
      AnnotationWriteOperateService annotationWriteOperateService,
      AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationWriteOperateService = annotationWriteOperateService;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
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
    final String annotation =
        annotationWriteOperateService.updateMetaDataAnnotation(
            updateAnnotationGroupRequest,
            annotationTaskBlock.getAnnotation(),
            annotationTaskBlock.getAnnotationType().ordinal());
    annotationTaskBlock.setAnnotation(annotation);
    annotationTaskBlockRepository.save(annotationTaskBlock);
    return AnnotationConvert.convert2AnnotationBlockBratVO(annotationTaskBlock);
  }
}
