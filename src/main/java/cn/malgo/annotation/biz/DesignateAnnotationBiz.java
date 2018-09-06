package cn.malgo.annotation.biz;

import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationService;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InternalServerException;
import cn.malgo.service.exception.InvalidInputException;
import org.springframework.stereotype.Component;

@Component
public class DesignateAnnotationBiz extends BaseBiz<DesignateAnnotationRequest, String> {
  private final AnnotationService annotationService;

  public DesignateAnnotationBiz(AnnotationService annotationService) {
    this.annotationService = annotationService;
  }

  @Override
  protected void validateRequest(DesignateAnnotationRequest designateAnnotationRequest)
      throws InvalidInputException {
    if (designateAnnotationRequest.getIdList().size() == 0) {
      throw new InvalidInputException("invalid-id-list", "idList集合为空");
    }

    if (designateAnnotationRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-user-id", "userId参数不正确");
    }
  }

  @Override
  protected String doBiz(DesignateAnnotationRequest designateAnnotationRequest) {
    try {
      annotationService.designateAnnotationNew(designateAnnotationRequest);
    } catch (Exception ex) {
      throw new InternalServerException("batch-designate-failed: " + ex.getMessage());
    }

    return "";
  }
}
