package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;

@Service
@Slf4j
public class AnnotationBlockServiceImpl implements AnnotationBlockService {
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public AnnotationBlockServiceImpl(
      final AnnotationCombineRepository annotationCombineRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
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
    }

    return result;
  }

  @Override
  public AnnotationTaskBlock saveAnnotation(final AnnotationCombine annotationCombine) {
    Objects.requireNonNull(annotationCombine);

    try {
      final AnnotationTaskBlock block =
          annotationTaskBlockRepository.findByAnnotationTypeEqualsAndTextEquals(
              AnnotationTypeEnum.getByValue(annotationCombine.getAnnotationType()),
              annotationCombine.getTerm());
      block.setAnnotation(annotationCombine.getReviewedAnnotation());
      block.setState(AnnotationTaskState.ANNOTATED);
      return annotationTaskBlockRepository.save(block);
    } catch (EntityNotFoundException ex) {
      log.warn("annotation combine {} not found in task block", annotationCombine.getId());
      return null;
    }
  }
}
