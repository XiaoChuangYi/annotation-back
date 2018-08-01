package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
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
    //
    for (Annotation annotation : annotationList) {
      List<Entity> entities = annotation.getDocument().getEntities();
      for (int i = 0; i < entities.size(); i++) {
        for (int k = 0; k < entities.size(); k++) {
          if (i == k) {
            continue;
          }
          if (AnnotationConvert.isCross(entities.get(i), entities.get(k))) {
            results.add(
                new WordErrorWithPosition(
                    entities.get(i).getTerm(),
                    entities.get(i).getType(),
                    new BratPosition(entities.get(i).getStart(), entities.get(i).getEnd()),
                    annotation,
                    null));
          }
        }
      }
    }
    return postProcess(results, this.batchSize);
  }
}
