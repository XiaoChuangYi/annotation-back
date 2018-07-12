package cn.malgo.annotation.biz;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.CountAnnotationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/31. */
@Component
public class CountAnnotationBiz extends BaseBiz<CountAnnotationRequest, Integer> {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public CountAnnotationBiz(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(CountAnnotationRequest countAnnotationRequest)
      throws InvalidInputException {
    if (countAnnotationRequest.getAnnotationTypes() == null) {
      throw new InvalidInputException("invalid-annotation-types", "参数annotationTypes当前值无效");
    }
  }

  @Override
  protected Integer doBiz(CountAnnotationRequest countAnnotationRequest) {
    int num;
    if (countAnnotationRequest.getAnnotationTypes().size() > 0) {
      num =
          annotationCombineRepository.countAllByAnnotationTypeInAndStateEquals(
              countAnnotationRequest.getAnnotationTypes(),
              AnnotationCombineStateEnum.unDistributed.name());
    } else {
      num =
          annotationCombineRepository.countAllByStateIn(
              AnnotationCombineStateEnum.unDistributed.name());
    }
    return num;
  }
}
