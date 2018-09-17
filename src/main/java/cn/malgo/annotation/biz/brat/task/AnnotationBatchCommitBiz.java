package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.anno.BatchCommitAnnotationRequest;
import cn.malgo.annotation.service.AnnotationService;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnnotationBatchCommitBiz
    extends TransactionalBiz<BatchCommitAnnotationRequest, Object> {

  private final AnnotationRepository annotationRepository;
  private final AnnotationService annotationService;

  @Autowired
  public AnnotationBatchCommitBiz(
      final AnnotationRepository annotationRepository, final AnnotationService annotationService) {
    this.annotationRepository = annotationRepository;
    this.annotationService = annotationService;
  }

  @Override
  protected void validateRequest(BatchCommitAnnotationRequest request)
      throws InvalidInputException {
    if (request.getIds() == null || request.getIds().size() == 0) {
      throw new InvalidInputException("ids-is-empty", "ids为空");
    }
  }

  @Override
  protected Object doBiz(BatchCommitAnnotationRequest request, UserDetails user) {
    final List<AnnotationNew> annotationNews = annotationRepository.findAllById(request.getIds());
    if (annotationNews.size() != request.getIds().size()) {
      log.warn("batch commit has error id");
    }
    annotationNews
        .stream()
        .forEach(annotationNew -> annotationService.annotationSingleCommit(user, annotationNew));
    return null;
  }
}
