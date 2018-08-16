package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.CleanOutBlockRequest;
import cn.malgo.annotation.service.AnnotationSummaryService;
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
  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationSummaryService annotationSummaryService;

  public CleanOutBlockBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationSummaryService annotationSummaryService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationSummaryService = annotationSummaryService;
  }

  @Override
  protected void validateRequest(CleanOutBlockRequest cleanoutBlockRequest)
      throws InvalidInputException {}

  @Override
  protected Object doBiz(CleanOutBlockRequest cleanOutBlockRequest, UserDetails user) {
    final AnnotationTask task = annotationTaskRepository.getOne(cleanOutBlockRequest.getTaskId());
    if (task == null) {
      throw new InvalidInputException("invalid-task-id", "无效的任务id");
    }
    if (task.getState() == AnnotationTaskState.FINISHED
        && annotationTaskBlockRepository
            .findByTaskBlocks_Task_IdEquals(task.getId())
            .parallelStream()
            .allMatch(
                annotationTaskBlock ->
                    annotationTaskBlock.getState() == AnnotationTaskState.PRE_CLEAN)) {
      // todo 计算准确率
      annotationSummaryService.updateAnnotationPrecisionAndRecallRate(task);
      updateBlockState(cleanOutBlockRequest);
    }
    // annotationTaskBlockRepository.copyDataToRelease();
    return task;
  }

  private void updateBlockState(CleanOutBlockRequest cleanOutBlockRequest) {
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
  }
}
