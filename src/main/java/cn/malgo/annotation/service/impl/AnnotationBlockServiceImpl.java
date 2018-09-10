package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.TaskBlock;
import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnotationBlockServiceImpl implements AnnotationBlockService {

  private final AnnotationRepository annotationRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public AnnotationBlockServiceImpl(
      final AnnotationRepository annotationRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationTaskRepository annotationTaskRepository) {
    this.annotationRepository = annotationRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  public Pair<AnnotationTaskBlock, Boolean> getOrCreateAnnotation(
      final AnnotationTypeEnum annotationType,
      final String text,
      final boolean createAnnotationNew) {
    final Pair<AnnotationTaskBlock, Boolean> result =
        annotationTaskBlockRepository.getOrCreateBlock(annotationType, text);

    if (createAnnotationNew && result.getRight()) {
      final AnnotationNew annotationNew = new AnnotationNew();
      annotationNew.setAnnotationType(annotationType);
      annotationNew.setTerm(text);
      annotationNew.setBlockId(result.getLeft().getId());
      annotationRepository.save(annotationNew);

      result.getLeft().setState(AnnotationTaskState.DOING);
      return Pair.of(annotationTaskBlockRepository.save(result.getLeft()), result.getRight());
    }

    return result;
  }

  @Override
  public void saveAnnotation(final AnnotationNew annotationNew) {
    Objects.requireNonNull(annotationNew);

    final AnnotationTaskBlock block =
        annotationTaskBlockRepository.getOne(annotationNew.getBlockId());

    if (block == null) {
      log.warn("annotation combine {} not found in task block", annotationNew.getId());
      return;
    }

    block.setAnnotation(annotationNew.getFinalAnnotation());
    block.setState(AnnotationTaskState.ANNOTATED);
    block.setAssignee(annotationNew.getAssignee());

    updateTaskState(annotationTaskBlockRepository.save(block));
  }

  private void batchUpdateTaskAndDocState(final List<AnnotationTaskBlock> blocks) {
    blocks
        .stream()
        .flatMap(taskDocBlock -> taskDocBlock.getTaskBlocks().stream())
        .map(TaskBlock::getTask)
        .collect(Collectors.toSet())
        .forEach(annotationTaskRepository::updateState);
  }

  @Override
  public void updateTaskState(final AnnotationTaskBlock block) {
    block
        .getTaskBlocks()
        .stream()
        .map(TaskBlock::getTask)
        .collect(Collectors.toSet())
        .forEach(annotationTaskRepository::updateState);
  }

  @Override
  public AnnotationNew resetBlock(
      final AnnotationTaskBlock block,
      final AnnotationBlockActionEnum action,
      final String comment) {
    block.setState(AnnotationTaskState.DOING);
    updateTaskState(annotationTaskBlockRepository.save(block));

    final AnnotationNew annotationNew = new AnnotationNew();
    annotationNew.setTerm(block.getText());
    annotationNew.setAnnotationType(block.getAnnotationType());
    annotationNew.setAssignee(0);
    annotationNew.setManualAnnotation(block.getAnnotation());
    annotationNew.setFinalAnnotation(block.getAnnotation());
    annotationNew.setState(AnnotationStateEnum.UN_DISTRIBUTED);
    annotationNew.setBlockId(block.getId());
    annotationNew.setComment(comment);
    return annotationRepository.save(annotationNew);
  }
}
