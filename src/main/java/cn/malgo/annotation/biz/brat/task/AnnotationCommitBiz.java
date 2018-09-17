package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.annotation.service.AnnotationService;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import cn.malgo.service.model.UserDetails;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnnotationCommitBiz extends BaseBiz<CommitAnnotationRequest, Object> {

  private final AnnotationRepository annotationRepository;
  private final AnnotationService annotationService;

  @Autowired
  public AnnotationCommitBiz(
      final AnnotationRepository annotationRepository, final AnnotationService annotationService) {
    this.annotationRepository = annotationRepository;
    this.annotationService = annotationService;
  }

  @Override
  protected void validateRequest(CommitAnnotationRequest commitAnnotationRequest)
      throws InvalidInputException {
    if (commitAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (commitAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected Object doBiz(CommitAnnotationRequest request, UserDetails user) {
    Optional<AnnotationNew> optional = annotationRepository.findById(request.getId());

    if (optional.isPresent()) {
      final AnnotationNew annotationNew = optional.get();
      annotationService.annotationSingleCommit(user, annotationNew);
      return null;
    }
    throw new NotFoundException("annotation-not-found", request.getId() + "不存在");
  }
}
