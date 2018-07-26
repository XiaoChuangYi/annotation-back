package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.AddBlocksToTaskRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class AddBlocksToTaskBiz extends TransactionalBiz<AddBlocksToTaskRequest, Object> {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public AddBlocksToTaskBiz(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(AddBlocksToTaskRequest request) throws InvalidInputException {
    if (request.getTaskId() <= 0) {
      throw new InvalidInputException("invalid-task-id", "无效的参数taskId");
    }
    if (request.getBlockIds().size() == 0) {
      throw new InvalidInputException("block-id-list-empty", "blocks集合为空");
    }
  }

  @Override
  protected Object doBiz(AddBlocksToTaskRequest request, UserDetails user) {
    final AnnotationTask annotationTask = annotationTaskRepository.getOne(request.getTaskId());
    annotationTaskBlockRepository
        .findAllById(request.getBlockIds())
        .stream()
        .forEach(
            annotationTaskBlock -> {
              if (annotationTaskBlock.getState() == AnnotationTaskState.ANNOTATED
                  || annotationTaskBlock.getState() == AnnotationTaskState.FINISHED) {
                annotationTaskBlock.setState(AnnotationTaskState.DOING);
              }
              annotationTask.addBlock(annotationTaskBlock);
            });
    return annotationTaskRepository.updateState(annotationTask);
  }
}
