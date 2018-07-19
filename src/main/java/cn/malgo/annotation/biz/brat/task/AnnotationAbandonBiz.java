package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import cn.malgo.service.model.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

@Component
@RequirePermission(Permissions.ANNOTATE)
public class AnnotationAbandonBiz extends BaseBiz<AnnotationStateRequest, Object> {
  private final AnnotationCombineRepository annotationCombineRepository;

  public AnnotationAbandonBiz(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AnnotationStateRequest annotationStateRequest)
      throws InvalidInputException {
    if (annotationStateRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (annotationStateRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected Object doBiz(AnnotationStateRequest request, UserDetails user) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(request.getId());

    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();

      switch (annotationCombine.getStateEnum()) {
        case preAnnotation:
        case annotationProcessing:
          if (annotationCombine.getAssignee() != user.getId()) {
            throw new BusinessRuleException("permission-denied", "当前用户没有权限操作该条记录！");
          }

          break;

        default:
          throw new BusinessRuleException("invalid-state", "当前记录无法直接设定为'放弃'状态！");
      }

      annotationCombine.setState(AnnotationCombineStateEnum.abandon.name());
      annotationCombine.setCommitTimestamp(new Date());
      annotationCombine.setReviewedAnnotation(annotationCombine.getFinalAnnotation());
      annotationCombineRepository.save(annotationCombine);
    }

    throw new NotFoundException("annotation-not-found", request.getId() + "不存在");
  }
}
