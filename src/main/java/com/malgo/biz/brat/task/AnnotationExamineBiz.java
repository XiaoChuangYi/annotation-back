package com.malgo.biz.brat.task;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.AnnotationStateRequest;
import com.malgo.utils.AnnotationConvert;
import com.malgo.utils.OpLoggerUtil;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/3.
 */
@Component
public class AnnotationExamineBiz extends BaseBiz<AnnotationStateRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private int globalRole;
  private int globalUserId;

  public AnnotationExamineBiz(AnnotationCombineRepository annotationCombineRepository) {
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
      throw new BusinessRuleException("no-privilege-handle-current-record", "当前用户无权限进行该操作!");
    }
  }

  @Override
  protected Object doBiz(AnnotationStateRequest annotationStateRequest) {
    Optional<AnnotationCombine> optional = annotationCombineRepository
        .findById(annotationStateRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preExamine.name())) {
        boolean changed = AnnotationConvert
            .compareAnnotation(annotationCombine.getFinalAnnotation(),
                annotationCombine.getReviewedAnnotation());
        if (changed) {//相同
          annotationCombine.setState(AnnotationCombineStateEnum.examinePass.name());
        } else {//不同
          annotationCombine.setState(AnnotationCombineStateEnum.errorPass.name());
        }
      }
      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.abandon.name())) {
        annotationCombine.setState(AnnotationCombineStateEnum.innerAnnotation.name());
      }
      annotationCombineRepository.save(annotationCombine);
      OpLoggerUtil.info(globalUserId, globalRole, "examine-annotation", "success");
    }
    return null;
  }
}
