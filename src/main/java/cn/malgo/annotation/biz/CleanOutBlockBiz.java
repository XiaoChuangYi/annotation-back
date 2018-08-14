package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.CleanOutBlockRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class CleanOutBlockBiz extends TransactionalBiz<CleanOutBlockRequest, Object> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationRepository annotationRepository;

  public CleanOutBlockBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationRepository annotationRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(CleanOutBlockRequest cleanoutBlockRequest)
      throws InvalidInputException {}

  @Override
  protected Object doBiz(CleanOutBlockRequest cleanOutBlockRequest, UserDetails user) {
    annotationRepository.saveAll(
        annotationRepository
            .findByTaskIdEqualsAndStateIn(
                cleanOutBlockRequest.getTaskId(),
                Collections.singletonList(AnnotationStateEnum.PRE_CLEAN))
            .parallelStream()
            .map(
                annotationNew -> {
                  annotationNew.setState(AnnotationStateEnum.CLEANED);
                  return annotationNew;
                })
            .collect(Collectors.toList()));
    annotationTaskBlockRepository.saveAll(
        annotationTaskBlockRepository
            .findByStateInAndTaskBlocks_Task_Id(
                Collections.singletonList(AnnotationTaskState.PRE_CLEAN),
                cleanOutBlockRequest.getTaskId())
            .parallelStream()
            .map(
                annotationTaskBlock -> {
                  annotationTaskBlock.setState(AnnotationTaskState.FINISHED);
                  return annotationTaskBlock;
                })
            .collect(Collectors.toList()));
    annotationTaskBlockRepository.copyDataToRelease();
    return null;
  }
}
