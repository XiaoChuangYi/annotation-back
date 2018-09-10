package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.block.BatchDeleteBlockRelationRequest;
import cn.malgo.annotation.request.block.BatchDeleteBlockRelationRequest.BlockRelation;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BatchDeleteBlockRelationBiz
    extends TransactionalBiz<BatchDeleteBlockRelationRequest, List<Long>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public BatchDeleteBlockRelationBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(BatchDeleteBlockRelationRequest request)
      throws InvalidInputException {
    if (request.getBlockRelationSet() == null || request.getBlockRelationSet().size() == 0) {
      throw new InvalidInputException("list-is-empty", "集合为空");
    }
  }

  @Override
  protected List<Long> doBiz(BatchDeleteBlockRelationRequest request, UserDetails user) {
    Map<Long, List<String>> batchDeleteMap =
        request
            .getBlockRelationSet()
            .stream()
            .collect(Collectors.toMap(BlockRelation::getId, BlockRelation::getRTags));
    final Set<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findByIdInAndStateIn(
            batchDeleteMap.keySet(),
            Arrays.asList(AnnotationTaskState.PRE_CLEAN, AnnotationTaskState.FINISHED));
    if (batchDeleteMap.size() != annotationTaskBlocks.size()) {
      log.warn("批量删除语料中存在非法ID: {}", batchDeleteMap.keySet());
    }
    annotationTaskBlocks
        .stream()
        .forEach(
            annotationTaskBlock -> {
              final List<String> rTags = batchDeleteMap.get(annotationTaskBlock.getId());
              if (rTags.size() > 0) {
                final String annotation =
                    AnnotationConvert.batchDeleteRelationAnnotation(
                        annotationTaskBlock.getAnnotation(), rTags);
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
