package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EntityOverlapErrorProvider extends BaseErrorProvider {
  private final int batchSize;

  public EntityOverlapErrorProvider(
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
    return postProcess(
        annotations.parallelStream().flatMap(this::findCrossEntities).collect(Collectors.toList()),
        batchSize);
  }

  private Stream<WordErrorWithPosition> findCrossEntities(final Annotation annotation) {
    return annotation
        .getDocument()
        .getEntities()
        .parallelStream()
        .flatMap(
            entity -> {
              for (Entity target : annotation.getDocument().getEntities()) {
                if (entity == target) {
                  continue;
                }

                if (AnnotationConvert.isCross(entity, target)) {
                  return Stream.of(
                      new WordErrorWithPosition(
                          entity.getTerm(),
                          entity.getType(),
                          new BratPosition(entity.getStart(), entity.getEnd()),
                          annotation,
                          entity.getTag()));
                }
              }

              return Stream.empty();
            });
  }
}
