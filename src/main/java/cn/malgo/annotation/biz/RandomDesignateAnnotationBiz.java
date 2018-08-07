package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.request.RandomDesignateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InternalServerException;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequirePermission(Permissions.ADMIN)
public class RandomDesignateAnnotationBiz
    extends BaseBiz<RandomDesignateAnnotationRequest, Object> {
  private final AnnotationService annotationCombineService;

  public RandomDesignateAnnotationBiz(AnnotationService annotationCombineService) {
    this.annotationCombineService = annotationCombineService;
  }

  @Override
  protected void validateRequest(RandomDesignateAnnotationRequest request)
      throws InvalidInputException {
    if (!(request.getAnnotationTypes() != null && request.getAnnotationTypes().size() > 0)) {
      throw new InvalidInputException("invalid-annotation-types", "标注类型集合为空");
    }

    if (!(request.getUserIdList() != null && request.getUserIdList().size() > 0)) {
      throw new InvalidInputException("invalid-user-id-list", "用户Id集合为空");
    }

    if (request.getNum() <= 0) {
      throw new InvalidInputException("invalid-num", "指派数量num参数小于等于了0");
    }

    if (request.getNum() < request.getUserIdList().size()) {
      throw new InvalidInputException("param-error", "指派数量num参数小于用户人数");
    }
  }

  @Override
  protected Object doBiz(RandomDesignateAnnotationRequest request) {
    try {
      annotationCombineService.randomDesignateAnnotationNew(request);
    } catch (Exception ex) {
      log.error("随机指派interface层异常", ex);
      throw new InternalServerException("随机指派interface层异常: " + ex.getMessage());
    }

    return "随机指派成功！";
  }
}
