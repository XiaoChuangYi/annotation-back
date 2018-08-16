package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.request.CountAnnotationRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class CountAnnotationBiz extends BaseBiz<CountAnnotationRequest, Integer> {

  private final AnnotationRepository annotationRepository;

  @Autowired
  public CountAnnotationBiz(AnnotationRepository annotationRepository) {
    this.annotationRepository = annotationRepository;
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
          annotationRepository.countAllByAnnotationTypeInAndState(
              countAnnotationRequest.getAnnotationTypes(), AnnotationStateEnum.UN_DISTRIBUTED);
    } else {
      num = annotationRepository.countAllByStateIn(AnnotationStateEnum.UN_DISTRIBUTED);
    }

    return num;
  }
}
