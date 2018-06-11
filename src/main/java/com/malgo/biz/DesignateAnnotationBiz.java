package com.malgo.biz;

import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InternalServiceException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.DesignateAnnotationRequest;
import com.malgo.service.AnnotationCombineService;
import com.malgo.utils.OpLoggerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/30. */
@Component
public class DesignateAnnotationBiz extends BaseBiz<DesignateAnnotationRequest, String> {

  private final AnnotationCombineService annotationCombineService;

  @Autowired
  public DesignateAnnotationBiz(AnnotationCombineService annotationCombineService) {
    this.annotationCombineService = annotationCombineService;
  }

  @Override
  protected void validateRequest(DesignateAnnotationRequest designateAnnotationRequest)
      throws InvalidInputException {
    if (designateAnnotationRequest.getIdList().size() == 0)
      throw new InvalidInputException("invalid-id-list", "idList集合为空");
    if (designateAnnotationRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-user-id", "userId参数不正确");
    }
  }

  @Override
  protected void authorize(
      int userId, int role, DesignateAnnotationRequest designateAnnotationRequest)
      throws BusinessRuleException {}

  @Override
  protected String doBiz(DesignateAnnotationRequest designateAnnotationRequest) {
    try {
      annotationCombineService.designateAnnotationCombine(designateAnnotationRequest);
    } catch (Exception ex) {
      throw new InternalServiceException("batch-designate-failed", ex.getMessage());
    }
    return "";
  }
}
