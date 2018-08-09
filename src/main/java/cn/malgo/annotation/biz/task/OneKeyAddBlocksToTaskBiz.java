package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.OneKeyAddBlocksToTaskRequest;
import cn.malgo.annotation.service.AddBlocksToTaskService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class OneKeyAddBlocksToTaskBiz
    extends TransactionalBiz<OneKeyAddBlocksToTaskRequest, Object> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationTaskRepository annotationTaskRepository;
  private final AddBlocksToTaskService addBlocksToTaskService;

  public OneKeyAddBlocksToTaskBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AddBlocksToTaskService addBlocksToTaskService,
      final AnnotationTaskRepository annotationTaskRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.addBlocksToTaskService = addBlocksToTaskService;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  protected void validateRequest(OneKeyAddBlocksToTaskRequest request)
      throws InvalidInputException {
    if (request.getTotalWordNum() > 1000 * 100) {
      throw new InvalidInputException("pre-suppose-total-word-num-too-large", "预取总字数太大");
    }
    if (request.getThreshold() > 0) {
      throw new InvalidInputException("threshold-must-be-zero", "阈值暂定为0");
    }
    if (request.getTaskId() < 0) {
      throw new InvalidInputException("invalid-task-id", "无效的taskId");
    }
  }

  @Override
  protected Object doBiz(OneKeyAddBlocksToTaskRequest request, UserDetails user) {
    final List<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findByStateIn(
            Collections.singletonList(AnnotationTaskState.CREATED),
            Sort.by(Direction.DESC, "nerFreshRate"));
    if (annotationTaskBlocks.size() > 0) {
      int wordNum = 0;
      final List<AnnotationTaskBlock> resultAnnotationTaskBlocks = new ArrayList<>();
      for (int k = 0; k < annotationTaskBlocks.size(); k++) {
        final AnnotationTaskBlock current = annotationTaskBlocks.get(k);
        if (wordNum >= request.getTotalWordNum()) {
          break;
        }
        wordNum += current.getText().length();
        resultAnnotationTaskBlocks.add(current);
      }
      final AnnotationTask annotationTask = annotationTaskRepository.getOne(request.getTaskId());
      return addBlocksToTaskService.addBlocksToTask(
          annotationTask,
          resultAnnotationTaskBlocks
              .stream()
              .map(annotationTaskBlock -> annotationTaskBlock.getId())
              .collect(Collectors.toList()));
    }
    return null;
  }
}
