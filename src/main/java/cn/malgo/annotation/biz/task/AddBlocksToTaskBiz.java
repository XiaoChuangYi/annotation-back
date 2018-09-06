package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.AddBlocksToTaskRequest;
import cn.malgo.annotation.service.AddBlocksToTaskService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class AddBlocksToTaskBiz extends TransactionalBiz<AddBlocksToTaskRequest, Object> {
  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AddBlocksToTaskService addBlocksToTaskService;

  public AddBlocksToTaskBiz(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AddBlocksToTaskService addBlocksToTaskService) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.addBlocksToTaskService = addBlocksToTaskService;
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
    final int num =
        annotationTaskBlockRepository
            .findByIdInAndTaskBlocks_Task_Id(request.getBlockIds(), request.getTaskId())
            .size();
    if (num == request.getBlockIds().size()) {
      throw new BusinessRuleException("this-task-had-those-blocks", "该批次中已经存在这些语料，无法继续新增");
    }
    if (annotationTaskBlockRepository
        .findAllById(request.getBlockIds())
        .parallelStream()
        .anyMatch(
            annotationTaskBlock ->
                annotationTaskBlock.getState() == AnnotationTaskState.ANNOTATED
                    || annotationTaskBlock.getState() == AnnotationTaskState.DOING)) {
      throw new BusinessRuleException("those-blocks-has-unexpected-state", "这些语料中含有不合法的状态，无法继续新增");
    }
    final AnnotationTask annotationTask = annotationTaskRepository.getOne(request.getTaskId());
    return addBlocksToTaskService.addBlocksToTask(annotationTask, request.getBlockIds());
  }
}
