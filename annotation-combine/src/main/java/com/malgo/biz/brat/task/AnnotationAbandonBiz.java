package com.malgo.biz.brat.task;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.AnnotationStateRequest;
import com.malgo.utils.OpLoggerUtil;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/3.
 */
@Component
public class AnnotationAbandonBiz extends BaseBiz<AnnotationStateRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private int globalRole;
  private int globalUserId;

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
    globalRole = role;
    globalUserId = userId;
    if (role > 2) {
      Optional<AnnotationCombine> optional = annotationCombineRepository
          .findById(annotationStateRequest.getId());
      if (optional.isPresent()) {
        AnnotationCombine annotationCombine = optional.get();
        if (userId != annotationCombine.getAssignee()) {
          throw new BusinessRuleException("no-permission-handle-current-record", "当前用户没有权限操作该条记录！");
        }
        if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preAnnotation)
            || annotationCombine.getState()
            .equals(AnnotationCombineStateEnum.annotationProcessing)) {
        } else {
          throw new BusinessRuleException("current-annotation-state-error", "当前记录无法直接设定为'放弃'状态！");
        }
      }
    }
  }

  @Override
  protected Object doBiz(AnnotationStateRequest annotationStateRequest) {
    Optional<AnnotationCombine> optional = annotationCombineRepository
        .findById(annotationStateRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      annotationCombine.setState(AnnotationCombineStateEnum.abandon.name());
      annotationCombineRepository.save(annotationCombine);
      OpLoggerUtil.info(globalUserId, globalRole, "abandon-annotation", "success");
    }
    return null;
  }
}
