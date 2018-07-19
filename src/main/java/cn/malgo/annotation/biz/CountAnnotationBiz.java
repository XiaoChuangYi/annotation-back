package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.CountAnnotationRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
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
