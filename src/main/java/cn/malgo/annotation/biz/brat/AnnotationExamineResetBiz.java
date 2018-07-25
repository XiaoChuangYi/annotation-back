package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AnnotationExamineResetBiz
    extends BaseBiz<AnnotationStateRequest, AnnotationCombineBratVO> {

  private final AnnotationCombineRepository annotationCombineRepository;

  public AnnotationExamineResetBiz(final AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AnnotationStateRequest request) throws InvalidInputException {
    if (request.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(AnnotationStateRequest request, UserDetails user) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(request.getId());
    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();
      annotationCombine.setFinalAnnotation("");
      annotationCombine.setManualAnnotation("");
      annotationCombine.setReviewedAnnotation("");
      return AnnotationConvert.convert2AnnotationCombineBratVO(
          annotationCombineRepository.save(annotationCombine));
    }
    return null;
  }
}
