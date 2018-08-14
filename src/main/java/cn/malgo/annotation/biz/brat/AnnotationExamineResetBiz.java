package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AnnotationExamineResetBiz extends BaseBiz<AnnotationStateRequest, AnnotationBratVO> {

  private final AnnotationRepository annotationRepository;

  public AnnotationExamineResetBiz(final AnnotationRepository annotationRepository) {
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(AnnotationStateRequest request) throws InvalidInputException {
    if (request.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationBratVO doBiz(AnnotationStateRequest request, UserDetails user) {
    final Optional<AnnotationNew> optional = annotationRepository.findById(request.getId());
    if (optional.isPresent()) {
      final AnnotationNew annotationNew = optional.get();
      annotationNew.setFinalAnnotation("");
      annotationNew.setManualAnnotation("");
      return AnnotationConvert.convert2AnnotationBratVO(annotationRepository.save(annotationNew));
    }
    return null;
  }
}
