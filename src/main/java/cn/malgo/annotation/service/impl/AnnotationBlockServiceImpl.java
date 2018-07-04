package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
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
}
