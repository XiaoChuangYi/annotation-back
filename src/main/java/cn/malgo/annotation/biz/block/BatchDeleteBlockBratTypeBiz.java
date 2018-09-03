package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.request.block.BatchDeleteBlockBratTypeRequest;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchDeleteBlockBratTypeBiz extends BaseBiz<BatchDeleteBlockBratTypeRequest, Object> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public BatchDeleteBlockBratTypeBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(BatchDeleteBlockBratTypeRequest request)
      throws InvalidInputException {
    if (request.getId() < 1) {
      throw new InvalidInputException("invalid-id", "无效的参数的id");
    }
  }

  @Override
  protected Object doBiz(BatchDeleteBlockBratTypeRequest request, UserDetails userDetails) {
    final AnnotationTaskBlock annotationTaskBlock =
        annotationTaskBlockRepository.getOne(request.getId());
    if (annotationTaskBlock == null) {
      throw new BusinessRuleException(
          "there is no record corresponding to the current id", "没有当前id对应的记录");
    }
    String newAnnotation =
        AnnotationConvert.batchDeleteRelationAnnotation(
            annotationTaskBlock.getAnnotation(), request.getRTags());
    newAnnotation = AnnotationConvert.batchDeleteEntityAnnotation(newAnnotation, request.getTags());
    annotationTaskBlock.setAnnotation(newAnnotation);
    return annotationTaskBlockRepository.save(annotationTaskBlock);
  }
}
