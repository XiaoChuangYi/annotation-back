package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskDocBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDocBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.TerminateTaskRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class TerminateTaskBiz extends TransactionalBiz<TerminateTaskRequest, Object> {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationTaskDocBlockRepository annotationTaskDocBlockRepository;
  private final AnnotationTaskDocRepository annotationTaskDocRepository;

  public TerminateTaskBiz(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationTaskDocBlockRepository annotationTaskDocBlockRepository,
      final AnnotationTaskDocRepository annotationTaskDocRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationTaskDocBlockRepository = annotationTaskDocBlockRepository;
    this.annotationTaskDocRepository = annotationTaskDocRepository;
  }

  @Override
  protected void validateRequest(TerminateTaskRequest terminateTaskRequest)
      throws InvalidInputException {}

  @Override
  protected Object doBiz(TerminateTaskRequest terminateTaskRequest, UserDetails user) {
    final Optional<AnnotationTask> optional =
        annotationTaskRepository.findById(terminateTaskRequest.getTaskId());
    if (optional.isPresent()) {
      final AnnotationTask annotationTask = optional.get();
      annotationTask.setState(AnnotationTaskState.FINISHED);
      annotationTaskRepository.save(annotationTask);

      final Set<AnnotationTaskBlock> blockSet =
          annotationTaskBlockRepository.findByStateInAndTaskDocs_TaskDoc_Task_Id(
              Arrays.asList(AnnotationTaskState.ANNOTATED), terminateTaskRequest.getTaskId());
      annotationTaskBlockRepository.saveAll(
          blockSet
              .stream()
              .map(
                  block -> {
                    block.setState(AnnotationTaskState.FINISHED);
                    return block;
                  })
              .collect(Collectors.toSet()));

      final Set<AnnotationTaskBlock> doingStateBlockSet =
          annotationTaskBlockRepository.findByStateInAndTaskDocs_TaskDoc_Task_Id(
              Arrays.asList(AnnotationTaskState.DOING), terminateTaskRequest.getTaskId());
      final List<AnnotationTaskDocBlock> annotationTaskDocBlocks =
          annotationTaskDocBlockRepository.findAllByBlockIn(doingStateBlockSet);
      annotationTaskDocRepository.deleteAll(
          annotationTaskDocRepository.findAllById(
              annotationTaskDocBlocks
                  .stream()
                  .map(annotationTaskDocBlock -> annotationTaskDocBlock.getId().getTaskDocId())
                  .collect(Collectors.toList())));
    }
    return null;
  }
}
