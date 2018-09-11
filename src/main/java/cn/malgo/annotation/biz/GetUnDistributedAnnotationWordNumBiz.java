package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class GetUnDistributedAnnotationWordNumBiz extends BaseBiz<Void, Integer> {

  private final AnnotationRepository annotationRepository;

  public GetUnDistributedAnnotationWordNumBiz(final AnnotationRepository annotationRepository) {
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(Void aVoid) throws InvalidInputException {}

  @Override
  protected Integer doBiz(Void aVoid, UserDetails user) {
    return annotationRepository
        .findAllByStateIn(
            Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED),
            Sort.by(Direction.ASC, "state"))
        .parallelStream()
        .mapToInt(annotationNew -> annotationNew.getTerm().length())
        .sum();
  }
}
