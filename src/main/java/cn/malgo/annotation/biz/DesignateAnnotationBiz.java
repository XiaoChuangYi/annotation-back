package cn.malgo.annotation.biz;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InternalServiceException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationCombineService;
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
  protected String doBiz(DesignateAnnotationRequest designateAnnotationRequest) {
    try {
      annotationCombineService.designateAnnotationCombine(designateAnnotationRequest);
    } catch (Exception ex) {
      throw new InternalServiceException("batch-designate-failed", ex.getMessage());
    }
    return "";
  }
}
