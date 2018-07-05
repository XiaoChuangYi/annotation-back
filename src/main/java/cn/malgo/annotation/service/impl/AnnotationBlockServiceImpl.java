package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.*;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDocBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AnnotationBlockServiceImpl implements AnnotationBlockService {
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationTaskDocRepository annotationTaskDocRepository;
  private final AnnotationTaskDocBlockRepository annotationTaskDocBlockRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public AnnotationBlockServiceImpl(
      final AnnotationCombineRepository annotationCombineRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationTaskDocBlockRepository annotationTaskDocBlockRepository,
      final AnnotationTaskDocRepository annotationTaskDocRepository,
      final AnnotationTaskRepository annotationTaskRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationTaskDocBlockRepository = annotationTaskDocBlockRepository;
    this.annotationTaskDocRepository = annotationTaskDocRepository;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  public Pair<AnnotationTaskBlock, Boolean> getOrCreateAnnotation(
      final AnnotationTypeEnum annotationType, final String text) {
    final Pair<AnnotationTaskBlock, Boolean> result =
        annotationTaskBlockRepository.getOrCreateBlock(annotationType, text);

    if (result.getRight()) {
      final AnnotationCombine annotationCombine = new AnnotationCombine();
      annotationCombine.setAnnotationType(annotationType.ordinal());
      annotationCombine.setTerm(text);
      annotationCombineRepository.save(annotationCombine);

      result.getLeft().setState(AnnotationTaskState.DOING);
      return Pair.of(annotationTaskBlockRepository.save(result.getLeft()), result.getRight());
    }

    return result;
  }

  @Override
  public void saveAnnotation(final AnnotationCombine annotationCombine) {
    Objects.requireNonNull(annotationCombine);

    final AnnotationTaskBlock block =
        annotationTaskBlockRepository.getOneByAnnotationTypeEqualsAndTextEquals(
            AnnotationTypeEnum.getByValue(annotationCombine.getAnnotationType()),
            annotationCombine.getTerm());

    if (block == null) {
      log.warn("annotation combine {} not found in task block", annotationCombine.getId());
      return;
    }

    block.setAnnotation(annotationCombine.getReviewedAnnotation());
    block.setState(AnnotationTaskState.ANNOTATED);

    // 更新所有对应的TaskDoc的状态
    final List<AnnotationTaskDocBlock> taskDocBlocks =
        annotationTaskDocBlockRepository.findByBlockEquals(
            annotationTaskBlockRepository.save(block));

    taskDocBlocks.forEach(
        taskDocBlock -> annotationTaskDocRepository.updateState(taskDocBlock.getTaskDoc()));

    taskDocBlocks
        .stream()
        .map(taskDocBlock -> taskDocBlock.getTaskDoc().getTask())
        .collect(Collectors.toSet())
        .forEach(annotationTaskRepository::updateState);
  }
}
