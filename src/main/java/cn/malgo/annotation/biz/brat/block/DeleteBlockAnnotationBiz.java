package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class DeleteBlockAnnotationBiz
    extends BaseBlockAnnotationBiz<DeleteAnnotationGroupRequest, AnnotationBlockBratVO> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationWriteOperateService annotationWriteOperateService;

  public DeleteBlockAnnotationBiz(
      AnnotationTaskBlockRepository annotationTaskBlockRepository,
      AnnotationWriteOperateService annotationWriteOperateService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationWriteOperateService = annotationWriteOperateService;
  }

  @Override
  protected void validateRequest(DeleteAnnotationGroupRequest deleteAnnotationGroupRequest)
      throws InvalidInputException {}

  @Override
  AnnotationBlockBratVO doInternalProcess(
      int role,
      AnnotationTaskBlock annotationTaskBlock,
      DeleteAnnotationGroupRequest deleteAnnotationGroupRequest) {
    final String annotation =
        annotationWriteOperateService.deleteMetaDataAnnotation(
            deleteAnnotationGroupRequest,
            annotationTaskBlock.getAnnotation(),
            annotationTaskBlock.getAnnotationType().ordinal());
    annotationTaskBlock.setAnnotation(annotation);
    annotationTaskBlockRepository.save(annotationTaskBlock);
    return AnnotationConvert.convert2AnnotationBlockBratVO(annotationTaskBlock);
  }
}
