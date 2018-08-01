package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.FixAnnotationEntity;
import cn.malgo.annotation.dto.error.FixAnnotationErrorContext;
import cn.malgo.annotation.dto.error.FixAnnotationErrorData;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import cn.malgo.service.exception.InvalidInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RelationOverlapErrorProvider extends BaseErrorProvider {
  private final int batchSize;

  public RelationOverlapErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository,
      @Value("${malgo.annotation.fix-log-batch-size}") final int batchSize) {
    super(annotationFixLogRepository);
    this.batchSize = batchSize;
  }

  @Override
  public AnnotationErrorEnum getErrorEnum() {
    return AnnotationErrorEnum.ENTITY_OVERLAP;
  }

  @Override
  public List<AlgorithmAnnotationWordError> find(List<Annotation> annotations) {
    final List<Annotation> annotationList =
        annotations
            .stream()
            .filter(
                annotation ->
                    annotation.getAnnotationType() == AnnotationTypeEnum.relation
                        && AnnotationConvert.isCrossAnnotation(annotation.getAnnotation()))
            .collect(Collectors.toList());
    final List<WordErrorWithPosition> results = new ArrayList<>();

    for (Annotation annotation : annotationList) {
      for (Entity entity : annotation.getDocument().getEntities()) {
        results.add(
            new WordErrorWithPosition(
                entity.getTerm(),
                entity.getType(),
                new BratPosition(entity.getStart(), entity.getEnd()),
                annotation,
                null));
      }
    }
    return postProcess(results, this.batchSize);
  }
}
