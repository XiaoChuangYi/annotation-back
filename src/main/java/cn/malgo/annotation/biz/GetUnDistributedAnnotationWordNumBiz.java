package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.anno.GetUnDistributedAnnotationRequest;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.stream.Collectors;
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
    if (request.getAnnotationTypes() == null || request.getAnnotationTypes().size() == 0) {
      throw new InvalidInputException("annotation-types-is-empty", "无效的参数annotationTypes");
    }
  }

  @Override
  protected Integer doBiz(GetUnDistributedAnnotationRequest request, UserDetails user) {
    return annotationRepository
        .findAllByStateInAndAnnotationTypeIn(
            Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED),
            request
                .getAnnotationTypes()
                .parallelStream()
                .map(AnnotationTypeEnum::valueOf)
                .collect(Collectors.toList()),
            Sort.by(Direction.ASC, "state"))
        .parallelStream()
        .mapToInt(annotationNew -> annotationNew.getTerm().length())
        .sum();
  }
}
