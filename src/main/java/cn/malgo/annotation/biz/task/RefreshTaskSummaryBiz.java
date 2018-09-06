package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.request.task.TerminateTaskRequest;
import cn.malgo.annotation.service.AnnotationSummaryService;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class RefreshTaskSummaryBiz
    extends TransactionalBiz<TerminateTaskRequest, AnnotationTaskVO> {
  private final AnnotationSummaryService annotationSummaryService;
  private final AnnotationTaskRepository taskRepository;

  public RefreshTaskSummaryBiz(
      final AnnotationSummaryService annotationSummaryService,
      final AnnotationTaskRepository taskRepository) {
    this.annotationSummaryService = annotationSummaryService;
    this.taskRepository = taskRepository;
  }

  @Override
  protected void validateRequest(TerminateTaskRequest terminateTaskRequest)
      throws InvalidInputException {}

  @Override
  protected AnnotationTaskVO doBiz(TerminateTaskRequest request) {
    final Optional<AnnotationTask> optional = taskRepository.findById(request.getTaskId());

    if (optional.isPresent()) {
      final AnnotationTask annotationTask = optional.get();
      return new AnnotationTaskVO(
          annotationSummaryService.updateTaskSummary(annotationTask.getId()));
    }

    throw new NotFoundException("task-not-found", request.getTaskId() + "未找到");
  }
}
