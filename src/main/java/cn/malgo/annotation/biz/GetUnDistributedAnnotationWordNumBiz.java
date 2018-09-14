package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.anno.GetUnDistributedAnnotationRequest;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class GetUnDistributedAnnotationWordNumBiz
    extends BaseBiz<GetUnDistributedAnnotationRequest, Integer> {

  private final AnnotationRepository annotationRepository;

  public GetUnDistributedAnnotationWordNumBiz(final AnnotationRepository annotationRepository) {
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(GetUnDistributedAnnotationRequest request)
      throws InvalidInputException {
    if (StringUtils.isBlank(request.getAnnotationType())) {
      throw new InvalidInputException("invalid-annotation-type", "无效的参数annotationType");
    }
  }

  @Override
  protected Integer doBiz(GetUnDistributedAnnotationRequest request, UserDetails user) {
    return annotationRepository
        .findAllByStateInAndAnnotationTypeEquals(
            Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED),
            AnnotationTypeEnum.valueOf(request.getAnnotationType()),
            Sort.by(Direction.ASC, "state"))
        .parallelStream()
        .mapToInt(annotationNew -> annotationNew.getTerm().length())
        .sum();
  }
}
