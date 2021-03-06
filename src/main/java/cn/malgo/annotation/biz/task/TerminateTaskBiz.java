package cn.malgo.annotation.biz.task;

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
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
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
      throw new InvalidInputException("invalid-task-id", "???????????????taskId");
    }
  }

  @Override
  protected Object doBiz(TerminateTaskRequest request, UserDetails user) {
    if (!request.isForceTerminate()
        && annotationTaskBlockRepository.countAnnotationTaskBlocksByStateIn(
                Collections.singletonList(AnnotationTaskState.PRE_CLEAN))
            > 0) {
      throw new BusinessRuleException(
          "no linguistic data to be cleaned is allowed before ending batch", "???????????????????????????????????????????????????");
    }

    final Optional<AnnotationTask> optional =
        annotationTaskRepository.findById(request.getTaskId());
    if (optional.isPresent()) {
      final AnnotationTask annotationTask = optional.get();
      final AnnotationTaskState state = annotationTask.getState();
      if (state == AnnotationTaskState.FINISHED || state == AnnotationTaskState.CREATED) {
        throw new InvalidInputException("invalid-task-state", state + "??????????????????");
      }

      annotationTask.setState(AnnotationTaskState.FINISHED);
      annotationTaskRepository.save(annotationTask);

      final Set<AnnotationTaskBlock> blockSet =
          annotationTaskBlockRepository.findByStateInAndTaskBlocks_Task_Id(
              Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.DOING),
              request.getTaskId());

      // ??????doing/annotated?????????blocks??????task?????????
      taskBlockRepository.deleteInBatch(
          taskBlockRepository.findByTask_IdAndBlock_StateIn(
              request.getTaskId(), Arrays.asList(AnnotationTaskState.DOING)));
      // block?????? doing??????????????????????????????created?????????annotated->pre_clean
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

  // ????????????submitted??????????????????pre_clean,???????????????????????????delete_token????????????
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
