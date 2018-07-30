package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.AddBlocksToTaskRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class AddBlocksToTaskBiz extends TransactionalBiz<AddBlocksToTaskRequest, Object> {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationCombineRepository annotationCombineRepository;

  public AddBlocksToTaskBiz(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationCombineRepository annotationCombineRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationCombineRepository = annotationCombineRepository;
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
    final AnnotationTask annotationTask = annotationTaskRepository.getOne(request.getTaskId());
    final List<AnnotationCombine> annotationCombines = new ArrayList<>();
    annotationTaskBlockRepository
        .findAllById(request.getBlockIds())
        .stream()
        .forEach(
            annotationTaskBlock -> {
              annotationTaskBlock.setState(AnnotationTaskState.DOING);
              final AnnotationCombine annotationCombine = new AnnotationCombine();
              annotationCombine.setTerm(annotationTaskBlock.getText());
              annotationCombine.setAnnotationType(
                  annotationTaskBlock.getAnnotationType().ordinal());
              annotationCombine.setAssignee(0);
              annotationCombine.setManualAnnotation(annotationTaskBlock.getAnnotation());
              annotationCombine.setFinalAnnotation(annotationTaskBlock.getAnnotation());
              annotationCombine.setReviewedAnnotation(annotationTaskBlock.getAnnotation());
              annotationCombine.setState(AnnotationCombineStateEnum.unDistributed.name());
              annotationCombine.setBlockId(annotationTaskBlock.getId());
              annotationCombine.setComment(
                  String.format(
                      "[block:{%d}] add to [task:{%d}]",
                      annotationTaskBlock.getId(), annotationTask.getId()));
              annotationCombines.add(annotationCombine);
              annotationTask.addBlock(annotationTaskBlock);
            });
    annotationCombineRepository.saveAll(annotationCombines);
    return annotationTaskRepository.updateState(annotationTask);
  }
}
