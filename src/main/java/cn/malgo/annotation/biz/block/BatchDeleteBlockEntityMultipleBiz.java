package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.BatchDeleteEntityMultipleRequest;
import cn.malgo.annotation.request.BatchDeleteEntityMultipleRequest.EntityMultipleType;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.service.annotation.RequirePermission;
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
@RequirePermission(Permissions.ADMIN)
public class BatchDeleteBlockEntityMultipleBiz
    extends TransactionalBiz<BatchDeleteEntityMultipleRequest, List<Long>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public BatchDeleteBlockEntityMultipleBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(BatchDeleteEntityMultipleRequest request)
      throws InvalidInputException {
    if (request.getEntityMultipleTypeSet() == null
        || request.getEntityMultipleTypeSet().size() == 0) {
      throw new InvalidInputException("list-is-empty", "集合为空");
    }
  }

  @Override
  protected List<Long> doBiz(BatchDeleteEntityMultipleRequest request, UserDetails user) {
    Map<Long, List<String>> batchDeleteMap =
        request
            .getEntityMultipleTypeSet()
            .stream()
            .collect(Collectors.toMap(EntityMultipleType::getId, EntityMultipleType::getTags));
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
              final List<String> tags = batchDeleteMap.get(annotationTaskBlock.getId());
              if (tags.size() > 0) {
                final String annotation =
                    AnnotationConvert.batchDeleteEntityAnnotation(
                        annotationTaskBlock.getAnnotation(), tags);
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
