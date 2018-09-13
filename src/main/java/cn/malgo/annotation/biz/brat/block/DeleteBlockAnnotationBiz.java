package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.service.exception.InvalidInputException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class DeleteBlockAnnotationBiz
    extends BaseBlockAnnotationBiz<DeleteAnnotationGroupRequest, AnnotationBlockBratVO> {
  private final AnnotationWriteOperateService annotationWriteOperateService;

  public DeleteBlockAnnotationBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationFactory annotationFactory,
      final ExtractAddAtomicTermService extractAddAtomicTermService,
      final AnnotationWriteOperateService annotationWriteOperateService) {
    super(annotationTaskBlockRepository, annotationFactory, extractAddAtomicTermService);

    this.annotationWriteOperateService = annotationWriteOperateService;
  }

  @Override
  protected void validateRequest(DeleteAnnotationGroupRequest deleteAnnotationGroupRequest)
      throws InvalidInputException {
    if (StringUtils.isAllBlank(
        deleteAnnotationGroupRequest.getReTag(), deleteAnnotationGroupRequest.getTag())) {
      throw new InvalidInputException("invalid-reTag-or-tag", "无效的参数tag或reTag");
    }
  }

  @Override
  AnnotationBlockBratVO doInternalProcess(
      AnnotationTaskBlock annotationTaskBlock,
      DeleteAnnotationGroupRequest deleteAnnotationGroupRequest) {
    final String annotation =
        annotationWriteOperateService.deleteMetaDataAnnotation(
            deleteAnnotationGroupRequest,
            annotationTaskBlock.getAnnotation(),
            annotationTaskBlock.getAnnotationType().ordinal());
    return AnnotationConvert.convert2AnnotationBlockBratVO(
        saveAnnotation(annotationTaskBlock, annotation));
  }
}
