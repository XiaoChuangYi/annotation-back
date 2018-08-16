package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.TaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.TerminateTaskRequest;
import cn.malgo.annotation.service.AnnotationSummaryService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class TerminateTaskBiz extends TransactionalBiz<TerminateTaskRequest, Object> {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final TaskBlockRepository taskBlockRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationSummaryService annotationSummaryService;
  private final AnnotationRepository annotationRepository;

  public TerminateTaskBiz(
      final AnnotationTaskRepository annotationTaskRepository,
      final TaskBlockRepository taskBlockRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationSummaryService annotationSummaryService,
      final AnnotationRepository annotationRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.taskBlockRepository = taskBlockRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationSummaryService = annotationSummaryService;
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(TerminateTaskRequest request) throws InvalidInputException {
    if (request.getTaskId() <= 0) {
      throw new InvalidInputException("invalid-task-id", "无效的参数taskId");
    }
  }

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
              Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.DOING),
              request.getTaskId());

      // 删除doing/annotated状态的blocks与该task的关系
      taskBlockRepository.deleteInBatch(
          taskBlockRepository.findByTask_IdAndBlock_StateIn(
              request.getTaskId(), Arrays.asList(AnnotationTaskState.DOING)));
      // block语料 doing状态的语料状态重置成created状态，annotated->pre_clean
      blockSet.forEach(
          block -> {
            if (block.getState() == AnnotationTaskState.ANNOTATED) {
              block.setState(AnnotationTaskState.PRE_CLEAN);
            }
            if (block.getState() == AnnotationTaskState.DOING) {
              block.setState(AnnotationTaskState.CREATED);
            }
            annotationTaskBlockRepository.save(block);
          });
      terminateAnnotationNew(annotationTask, blockSet);
      annotationSummaryService.updateTaskSummary(annotationTask.getId());
    }
    return null;
  }

  // 修改标注submitted状态的标注为pre_clean,其它状态的标注更新delete_token为当前值
  private void terminateAnnotationNew(
      AnnotationTask annotationTask, Set<AnnotationTaskBlock> blockSet) {
    final List<AnnotationNew> annotationNews =
        annotationRepository
            .findAllByTaskIdEqualsAndBlockIdIn(
                annotationTask.getId(),
                blockSet
                    .stream()
                    .map(annotationTaskBlock -> annotationTaskBlock.getId())
                    .collect(Collectors.toList()))
            .parallelStream()
            .map(
                annotationNew -> {
                  if (annotationNew.getState() == AnnotationStateEnum.SUBMITTED) {
                    annotationNew.setState(AnnotationStateEnum.PRE_CLEAN);
                  }
                  if (annotationNew.getState() == AnnotationStateEnum.UN_DISTRIBUTED
                      || annotationNew.getState() == AnnotationStateEnum.PRE_ANNOTATION
                      || annotationNew.getState() == AnnotationStateEnum.ANNOTATION_PROCESSING) {
                    annotationNew.setDeleteToken(new Date().getTime());
                    annotationNew.setState(AnnotationStateEnum.UN_DISTRIBUTED);
                  }
                  return annotationNew;
                })
            .collect(Collectors.toList());
    annotationRepository.saveAll(annotationNews);
  }
}
