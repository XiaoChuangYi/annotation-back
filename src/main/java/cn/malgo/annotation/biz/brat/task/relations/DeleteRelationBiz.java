package cn.malgo.annotation.biz.brat.task.relations;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeleteRelationBiz
    extends BaseRelationBiz<DeleteRelationRequest, AnnotationCombineBratVO> {
  @Override
  protected void validateRequest(DeleteRelationRequest deleteRelationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(deleteRelationRequest.getReTag())) {
      throw new InvalidInputException("invalid-reTag", "参数reTag为空");
    }
  }

  @Override
  AnnotationCombineBratVO doInternalProcess(
      RelationOperateService relationOperateService,
      AnnotationCombine annotationCombine,
      DeleteRelationRequest deleteRelationRequest) {
    relationOperateService.deleteRelation(annotationCombine, deleteRelationRequest);
    return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
  }
}
