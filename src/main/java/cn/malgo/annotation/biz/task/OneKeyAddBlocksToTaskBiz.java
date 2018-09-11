package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.OneKeyAddBlocksToTaskRequest;
import cn.malgo.annotation.service.AddBlocksToTaskService;
import cn.malgo.annotation.utils.BlockBatchIterator;
import cn.malgo.annotation.vo.AddBlocksToTaskVO;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class OneKeyAddBlocksToTaskBiz
    extends TransactionalBiz<OneKeyAddBlocksToTaskRequest, AddBlocksToTaskVO> {
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
  protected AddBlocksToTaskVO doBiz(OneKeyAddBlocksToTaskRequest request, UserDetails user) {
    final BlockBatchIterator it =
        new BlockBatchIterator(
            annotationTaskBlockRepository,
            Collections.singletonList(AnnotationTaskState.CREATED),
            1000,
            Sort.by(Direction.DESC, "nerFreshRate"));

    final List<AnnotationTaskBlock> blocks = new ArrayList<>();
    int wordNum = request.getTotalWordNum();
    while (it.hasNext() && wordNum > 0) {
      final List<AnnotationTaskBlock> batch = it.next();
      for (AnnotationTaskBlock block : batch) {
        blocks.add(block);
        wordNum -= block.getText().length();

        if (wordNum <= 0) {
          break;
        }
      }
    }

    if (blocks.size() > 0) {
      addBlocksToTaskService.addBlocksToTask(
          annotationTaskRepository.getOne(request.getTaskId()), blocks);
      return new AddBlocksToTaskVO(
          request.getTaskId(), blocks.size(), request.getTotalWordNum() - wordNum);
    }

    return new AddBlocksToTaskVO(request.getTaskId(), 0, 0);
  }
}
