package cn.malgo.annotation.biz;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InternalServiceException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.RandomDesignateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationCombineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/30. */
@Component
@Slf4j
public class RandomDesignateAnnotationBiz
    extends BaseBiz<RandomDesignateAnnotationRequest, Object> {

  private final AnnotationCombineService annotationCombineService;

  @Autowired
  public RandomDesignateAnnotationBiz(AnnotationCombineService annotationCombineService) {
    this.annotationCombineService = annotationCombineService;
  }

  @Override
  protected void validateRequest(RandomDesignateAnnotationRequest randomDesignateAnnotationRequest)
      throws InvalidInputException {

    if (!(randomDesignateAnnotationRequest.getAnnotationTypes() != null
        && randomDesignateAnnotationRequest.getAnnotationTypes().size() > 0)) {
      throw new InvalidInputException("invalid-annotation-types", "标注类型集合为空");
    }
    if (!(randomDesignateAnnotationRequest.getUserIdList() != null
        && randomDesignateAnnotationRequest.getUserIdList().size() > 0)) {
      throw new InvalidInputException("invalid-user-id-list", "用户Id集合为空");
    }
    if (randomDesignateAnnotationRequest.getNum() <= 0) {
      throw new InvalidInputException("invalid-num", "指派数量num参数小于等于了0");
    }

    if (randomDesignateAnnotationRequest.getNum()
        < randomDesignateAnnotationRequest.getUserIdList().size()) {
      throw new InvalidInputException("param-error", "指派数量num参数小于用户人数");
    }
  }

  @Override
  protected Object doBiz(RandomDesignateAnnotationRequest randomDesignateAnnotationRequest) {
    try {
      annotationCombineService.randomDesignateAnnotationCombine(randomDesignateAnnotationRequest);
    } catch (Exception ex) {
      log.error("随机指派interface层异常", ex);
      throw new InternalServiceException("随机指派interface层异常", ex.getMessage());
    }
    return "随机指派成功！";
  }
}
