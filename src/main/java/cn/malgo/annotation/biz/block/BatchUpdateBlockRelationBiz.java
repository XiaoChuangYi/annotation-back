package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.block.BatchUpdateBlockRelationRequest;
import cn.malgo.annotation.request.block.BatchUpdateBlockRelationRequest.BlockRelationUpdate;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchUpdateBlockRelationBiz
    extends TransactionalBiz<BatchUpdateBlockRelationRequest, List<Long>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public BatchUpdateBlockRelationBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(BatchUpdateBlockRelationRequest request)
      throws InvalidInputException {
    if (request.getBlockRelationSet() == null || request.getBlockRelationSet().size() == 0) {
      throw new InvalidInputException("list-is-empty", "集合为空");
    }
  }

  @Override
  protected List<Long> doBiz(BatchUpdateBlockRelationRequest request) {
    Map<Long, List<String>> batchUpdateMap =
        request
            .getBlockRelationSet()
            .stream()
            .collect(Collectors.toMap(BlockRelationUpdate::getId, BlockRelationUpdate::getRTags));
    final Set<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findByIdInAndStateIn(
            batchUpdateMap.keySet(),
            Arrays.asList(AnnotationTaskState.PRE_CLEAN, AnnotationTaskState.FINISHED));
    if (batchUpdateMap.size() != annotationTaskBlocks.size()) {
      log.warn("批量更新语料中存在非法ID: {}", batchUpdateMap.keySet());
    }
    annotationTaskBlocks
        .stream()
        .forEach(
            annotationTaskBlock -> {
              final List<String> rTags = batchUpdateMap.get(annotationTaskBlock.getId());
              if (rTags.size() > 0) {
                final String annotation =
                    AnnotationConvert.batchUpdateRelationAnnotation(
                        annotationTaskBlock.getAnnotation(), rTags, request.getNewType());
                annotationTaskBlock.setAnnotation(annotation);
              }
            });
    return annotationTaskBlockRepository
        .saveAll(annotationTaskBlocks)
        .stream()
        .map(annotationTaskBlock -> annotationTaskBlock.getId())
        .collect(Collectors.toList());
  }
}
