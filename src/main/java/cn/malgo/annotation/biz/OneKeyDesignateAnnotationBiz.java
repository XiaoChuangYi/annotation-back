package cn.malgo.annotation.biz;

import cn.malgo.annotation.request.OneKeyDesignateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationService;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OneKeyDesignateAnnotationBiz
    extends TransactionalBiz<OneKeyDesignateAnnotationRequest, Object> {

  private final AnnotationService annotationService;

  public OneKeyDesignateAnnotationBiz(final AnnotationService annotationService) {
    this.annotationService = annotationService;
  }

  @Override
  protected void validateRequest(OneKeyDesignateAnnotationRequest request)
      throws InvalidInputException {
    if (!(request.getUserIdList() != null && request.getUserIdList().size() > 0)) {
      throw new InvalidInputException("invalid-user-id-list", "用户Id集合为空");
    }
    if (request.getDesignateWordNum() <= 0) {
      throw new InvalidInputException("invalid-designate-word-num", "无效的designateWordNum");
    }
  }

  @Override
  protected Object doBiz(OneKeyDesignateAnnotationRequest request) {
    annotationService.oneKeyDesignateAnnotationNew(request);
    return "一键指派成功！";
  }
}
