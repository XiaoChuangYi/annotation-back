package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteAnnotationBiz
    extends BaseAnnotationBiz<DeleteAnnotationRequest, AnnotationCombineBratVO> {

  @Override
  protected void validateRequest(DeleteAnnotationRequest deleteAnnotationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(deleteAnnotationRequest.getTag())) {
      throw new InvalidInputException("invalid-tag", "参数tag为空");
    }
  }

  @Override
  AnnotationCombineBratVO doInternalProcess(
      AnnotationOperateService annotationOperateService,
      AnnotationCombine annotationCombine,
      DeleteAnnotationRequest deleteAnnotationRequest) {
    annotationOperateService.deleteAnnotation(annotationCombine, deleteAnnotationRequest);
    return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
  }
}
