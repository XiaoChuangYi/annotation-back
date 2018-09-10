package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.block.BatchAbandonBlockRequest;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnnotationBlockBatchAbandonBiz
    extends TransactionalBiz<BatchAbandonBlockRequest, Object> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public AnnotationBlockBatchAbandonBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(BatchAbandonBlockRequest request) throws InvalidInputException {
    if (request.getBlockIds() == null || request.getBlockIds().size() == 0) {
      throw new InvalidInputException("block-ids-is-empty", "参数blockIds为空");
    }
  }

  @Override
  protected Object doBiz(BatchAbandonBlockRequest request, UserDetails user) {
    final List<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findAllByStateInAndIdIn(
            Collections.singletonList(AnnotationTaskState.CREATED), request.getBlockIds());
    if (annotationTaskBlocks.size() != request.getBlockIds().size()) {
      throw new BusinessRuleException("block-ids-state-is-error", "参数blockIds中的部分id状态不对");
    }
    annotationTaskBlockRepository.saveAll(
        annotationTaskBlocks
            .stream()
            .map(
                annotationTaskBlock -> {
                  annotationTaskBlock.setState(AnnotationTaskState.FINISHED);
                  annotationTaskBlock.setAnnotation("");
                  return annotationTaskBlock;
                })
            .collect(Collectors.toList()));
    return null;
  }
}
