package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.AnnotationStateRequest;
import java.sql.Date;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/3. */
@Component
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
  protected void authorize(int userId, int role, AnnotationStateRequest annotationStateRequest)
      throws BusinessRuleException {
    if (role > AnnotationRoleStateEnum.auditor.getRole()) {
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(annotationStateRequest.getId());
      if (optional.isPresent()) {
        AnnotationCombine annotationCombine = optional.get();
        if (userId != annotationCombine.getAssignee()) {
          throw new BusinessRuleException("no-permission-handle-current-record", "当前用户没有权限操作该条记录！");
        }
        if (!StringUtils.equalsAny(
            annotationCombine.getState(),
            AnnotationCombineStateEnum.preAnnotation.name(),
            AnnotationCombineStateEnum.annotationProcessing.name())) {
          throw new BusinessRuleException("current-annotation-state-error", "当前记录无法直接设定为'放弃'状态！");
        }
      }
    }
  }

  @Override
  protected Object doBiz(AnnotationStateRequest annotationStateRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(annotationStateRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      annotationCombine.setState(AnnotationCombineStateEnum.abandon.name());
      annotationCombine.setCommitTimestamp(new Date(new java.util.Date().getTime()));
      annotationCombine.setReviewedAnnotation(annotationCombine.getFinalAnnotation());
      annotationCombineRepository.save(annotationCombine);
    }
    return null;
  }
}
