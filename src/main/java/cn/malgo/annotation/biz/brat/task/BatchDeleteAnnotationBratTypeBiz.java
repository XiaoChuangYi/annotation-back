package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.anno.BatchDeleteAnnotationBratTypeRequest;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BatchDeleteAnnotationBratTypeBiz
    extends TransactionalBiz<BatchDeleteAnnotationBratTypeRequest, AnnotationBratVO> {

  private final AnnotationRepository annotationRepository;

  public BatchDeleteAnnotationBratTypeBiz(final AnnotationRepository annotationRepository) {
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(BatchDeleteAnnotationBratTypeRequest request)
      throws InvalidInputException {
    if (request.getId() < 1) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationBratVO doBiz(BatchDeleteAnnotationBratTypeRequest request, UserDetails user) {
    Optional<AnnotationNew> optional = annotationRepository.findById(request.getId());
    if (!optional.isPresent()) {
      throw new BusinessRuleException(
          "there is no record corresponding to the current id", "没有当前id对应的记录");
    }
    AnnotationNew annotationNew = optional.get();
    String newAnnotation =
        AnnotationConvert.batchDeleteRelationAnnotation(
            annotationNew.getFinalAnnotation(), request.getRTags());
    newAnnotation = AnnotationConvert.batchDeleteEntityAnnotation(newAnnotation, request.getTags());
    annotationNew.setFinalAnnotation(newAnnotation);
    annotationNew = annotationRepository.save(annotationNew);
    return AnnotationConvert.convert2AnnotationBratVO(annotationNew);
  }
}
