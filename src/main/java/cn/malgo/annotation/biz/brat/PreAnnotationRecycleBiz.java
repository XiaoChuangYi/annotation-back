package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.AnnotationRecycleRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class PreAnnotationRecycleBiz extends BaseBiz<AnnotationRecycleRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;

  public PreAnnotationRecycleBiz(final AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AnnotationRecycleRequest request) throws InvalidInputException {
    if (request.getAnnotationIdList().size() == 0) {
      throw new InvalidInputException("invalid-annotation-id-list-id", "annotationIdList集合为空");
    }
  }

  @Override
  protected Object doBiz(AnnotationRecycleRequest request, UserDetails user) {
    final List<AnnotationCombine> annotationCombines =
        annotationCombineRepository.findAllById(request.getAnnotationIdList());
    if (annotationCombines.size() > 0) {
      final List<AnnotationCombine> annotationCombineList =
          annotationCombines
              .stream()
              .filter(
                  annotationCombine ->
                      StringUtils.equals(
                          annotationCombine.getState(),
                          AnnotationCombineStateEnum.preAnnotation.name()))
              .map(
                  annotationCombine -> {
                    annotationCombine.setState(AnnotationCombineStateEnum.unDistributed.name());
                    annotationCombine.setAssignee(1);
                    return annotationCombine;
                  })
              .collect(Collectors.toList());
      annotationCombineRepository.saveAll(annotationCombineList);
    }
    return null;
  }
}
