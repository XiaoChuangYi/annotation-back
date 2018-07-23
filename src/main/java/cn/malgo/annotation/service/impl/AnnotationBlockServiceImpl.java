package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.*;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDocBlock;
import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import java.util.Map;
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
      annotationCombine.setBlockId(result.getLeft().getId());
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

    updateTaskAndDocState(annotationTaskBlockRepository.save(block));
  }

  @Override
  public void saveAnnotationAll(List<AnnotationCombine> annotationCombines) {
    final List<Long> blockIds =
        annotationCombines
            .stream()
            .map(annotationCombine -> annotationCombine.getBlockId())
            .collect(Collectors.toList());
    final List<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findAllById(blockIds);
    if (annotationCombines.size() != annotationTaskBlocks.size()) {
      log.warn("some annotation ids are invalid {}", blockIds);
      return;
    }
    Map<Long, String> map =
        annotationCombines
            .stream()
            .collect(
                Collectors.toMap(
                    AnnotationCombine::getBlockId, AnnotationCombine::getReviewedAnnotation));
    annotationTaskBlocks
        .stream()
        .map(
            annotationTaskBlock -> {
              annotationTaskBlock.setAnnotation(map.get(annotationTaskBlock.getId()));
              annotationTaskBlock.setState(AnnotationTaskState.ANNOTATED);
              return annotationTaskBlock;
            })
        .collect(Collectors.toList());

    batchUpdateTaskAndDocState(annotationTaskBlockRepository.saveAll(annotationTaskBlocks));
  }

  private void batchUpdateTaskAndDocState(final List<AnnotationTaskBlock> blocks) {
    final List<AnnotationTaskDocBlock> taskDocBlocks =
        annotationTaskDocBlockRepository.findAllByBlockIn(blocks);
    taskDocBlocks.forEach(
        taskDocBlock -> annotationTaskDocRepository.updateState(taskDocBlock.getTaskDoc()));

    taskDocBlocks
        .stream()
        .map(taskDocBlock -> taskDocBlock.getTaskDoc().getTask())
        .collect(Collectors.toSet())
        .forEach(annotationTaskRepository::updateState);
  }

  @Override
  public void updateTaskAndDocState(final AnnotationTaskBlock block) {
    // 更新所有对应的TaskDoc的状态
    final List<AnnotationTaskDocBlock> taskDocBlocks =
        annotationTaskDocBlockRepository.findByBlockEquals(block);

    taskDocBlocks.forEach(
        taskDocBlock -> annotationTaskDocRepository.updateState(taskDocBlock.getTaskDoc()));

    taskDocBlocks
        .stream()
        .map(taskDocBlock -> taskDocBlock.getTaskDoc().getTask())
        .collect(Collectors.toSet())
        .forEach(annotationTaskRepository::updateState);
  }

  @Override
  public AnnotationCombine resetBlock(
      final AnnotationTaskBlock block,
      final AnnotationBlockActionEnum action,
      final String comment) {
    block.setState(AnnotationTaskState.DOING);
    updateTaskAndDocState(annotationTaskBlockRepository.save(block));

    final AnnotationCombine annotationCombine = new AnnotationCombine();
    annotationCombine.setTerm(block.getText());
    annotationCombine.setAnnotationType(block.getAnnotationType().ordinal());
    annotationCombine.setAssignee(0);
    annotationCombine.setManualAnnotation(block.getAnnotation());
    annotationCombine.setFinalAnnotation(block.getAnnotation());
    annotationCombine.setReviewedAnnotation(block.getAnnotation());
    annotationCombine.setState(
        action == AnnotationBlockActionEnum.RE_ANNOTATION
            ? AnnotationCombineStateEnum.unDistributed.name()
            : AnnotationCombineStateEnum.preExamine.name());
    annotationCombine.setBlockId(block.getId());
    annotationCombine.setComment(comment);
    return annotationCombineRepository.save(annotationCombine);
  }
}
