package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.request.AnnotationRecycleRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class PreAnnotationRecycleBiz extends BaseBiz<AnnotationRecycleRequest, Object> {

  private final AnnotationRepository annotationRepository;

  public PreAnnotationRecycleBiz(final AnnotationRepository annotationRepository) {
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(AnnotationRecycleRequest request) throws InvalidInputException {
    if (request.getAnnotationIdList().size() == 0) {
      throw new InvalidInputException("invalid-annotation-id-list-id", "annotationIdList集合为空");
    }
  }

  @Override
  protected Object doBiz(AnnotationRecycleRequest request, UserDetails user) {
    final List<AnnotationNew> annotationNews =
        annotationRepository.findAllById(request.getAnnotationIdList());
    if (annotationNews.size() > 0) {
      final List<AnnotationNew> annotationNewList =
          annotationNews
              .stream()
              .filter(
                  annotationNew ->
                      annotationNew.getState() == AnnotationStateEnum.PRE_ANNOTATION
                          || annotationNew.getState() == AnnotationStateEnum.ANNOTATION_PROCESSING)
              .map(
                  annotationNew -> {
                    annotationNew.setState(AnnotationStateEnum.UN_DISTRIBUTED);
                    annotationNew.setAssignee(0);
                    return annotationNew;
                  })
              .collect(Collectors.toList());
      annotationRepository.saveAll(annotationNewList);
    }
    return null;
  }
}
