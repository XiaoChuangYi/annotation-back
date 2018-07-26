package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.TaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.TerminateTaskRequest;
import cn.malgo.annotation.service.AnnotationSummaryService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class TerminateTaskBiz extends TransactionalBiz<TerminateTaskRequest, Object> {
  private final AnnotationTaskRepository annotationTaskRepository;
  private final TaskBlockRepository taskBlockRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationSummaryService annotationSummaryService;

  public TerminateTaskBiz(
      final AnnotationTaskRepository annotationTaskRepository,
      final TaskBlockRepository taskBlockRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationSummaryService annotationSummaryService) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.taskBlockRepository = taskBlockRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationSummaryService = annotationSummaryService;
  }

  @Override
  protected void validateRequest(TerminateTaskRequest terminateTaskRequest)
      throws InvalidInputException {}

  @Override
  protected Object doBiz(TerminateTaskRequest request, UserDetails user) {
    final Optional<AnnotationTask> optional =
        annotationTaskRepository.findById(request.getTaskId());
    if (optional.isPresent()) {
      final AnnotationTask annotationTask = optional.get();
      final AnnotationTaskState state = annotationTask.getState();
      if (state == AnnotationTaskState.FINISHED || state == AnnotationTaskState.CREATED) {
        throw new InvalidInputException("invalid-task-state", state + "不可以被结束");
      }

      annotationTask.setState(AnnotationTaskState.FINISHED);
      annotationTaskRepository.save(annotationTask);

      final Set<AnnotationTaskBlock> blockSet =
          annotationTaskBlockRepository.findByStateInAndTaskBlocks_Task_Id(
              Collections.singletonList(AnnotationTaskState.ANNOTATED), request.getTaskId());

      blockSet.forEach(
          block -> {
            block.setState(AnnotationTaskState.FINISHED);
            annotationTaskBlockRepository.save(block);
          });

      taskBlockRepository.deleteInBatch(
          taskBlockRepository.findByTask_IdAndBlock_StateIn(
              request.getTaskId(), Collections.singletonList(AnnotationTaskState.DOING)));

      annotationSummaryService.updateTaskSummary(annotationTask);
    }

    return null;
  }
}
