package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.brat.DeleteAnnotationGroupRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.service.AnnotationWriteOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteAnnotationBiz
    extends BaseAnnotationBiz<DeleteAnnotationGroupRequest, AnnotationBratVO> {

  private final AnnotationWriteOperateService annotationWriteOperateService;
  private final AnnotationRepository annotationRepository;

  public DeleteAnnotationBiz(
      final AnnotationWriteOperateService annotationWriteOperateService,
      final AnnotationRepository annotationRepository) {
    this.annotationWriteOperateService = annotationWriteOperateService;
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(DeleteAnnotationGroupRequest request)
      throws InvalidInputException {
    if (StringUtils.isAllBlank(request.getReTag(), request.getTag())) {
      throw new InvalidInputException("invalid-reTag-or-tag", "无效的参数tag或reTag");
    }
  }

  @Override
  AnnotationBratVO doInternalProcess(
      AnnotationOperateService annotationOperateService,
      AnnotationNew annotationNew,
      DeleteAnnotationGroupRequest request) {
    final String annotation =
        annotationWriteOperateService.deleteMetaDataAnnotation(
            request,
            annotationNew.getFinalAnnotation(),
            annotationNew.getAnnotationType().ordinal());
    annotationNew.setFinalAnnotation(annotation);
    annotationRepository.save(annotationNew);
    return AnnotationConvert.convert2AnnotationBratVO(annotationNew);
  }
}
