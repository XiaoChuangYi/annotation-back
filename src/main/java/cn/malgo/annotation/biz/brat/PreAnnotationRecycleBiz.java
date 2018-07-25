package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.AnnotationRecycleRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class PreAnnotationRecycleBiz extends BaseBiz<AnnotationRecycleRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;

  public PreAnnotationRecycleBiz(final AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AnnotationRecycleRequest request) throws InvalidInputException {
    if (request.getAnnotationId() <= 0) {
      throw new InvalidInputException("invalid-annotation-id", "无效的annotationId");
    }
  }

  @Override
  protected Object doBiz(AnnotationRecycleRequest request, UserDetails user) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(request.getAnnotationId());
    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();
      if (!StringUtils.equals(
          annotationCombine.getState(), AnnotationCombineStateEnum.preAnnotation.name())) {
        throw new BusinessRuleException("invalid-state-permission-deny", "该状态标注无法被回收");
      } else {
        annotationCombine.setState(AnnotationCombineStateEnum.unDistributed.name());
        annotationCombine.setAssignee(1);
      }
      annotationCombineRepository.save(annotationCombine);
    }
    return null;
  }
}
