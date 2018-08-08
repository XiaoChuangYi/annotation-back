package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.brat.BratPosition;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IsolatedEntityErrorProvider extends BaseErrorProvider {
  private final int batchSize;

  public IsolatedEntityErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository,
      @Value("${malgo.annotation.fix-log-batch-size}") final int batchSize) {
    super(annotationFixLogRepository);

    this.batchSize = batchSize;
  }

  @Override
  public AnnotationErrorEnum getErrorEnum() {
    return AnnotationErrorEnum.ISOLATED_ENTITY;
  }

  @Override
  public List<AlgorithmAnnotationWordError> find(final List<Annotation> annotations) {
    log.info("start finding isolated entity errors");

    final List<WordErrorWithPosition> results = new ArrayList<>();

    for (Annotation annotation : annotations) {
      final AnnotationDocument document = annotation.getDocument();
      final Set<String> usedTags = new HashSet<>();
      document
          .getRelationEntities()
          .forEach(
              entity -> {
                usedTags.add(entity.getSourceTag());
                usedTags.add(entity.getTargetTag());
              });

      document
          .getEntities()
          .stream()
          .filter(entity -> !entity.getType().equals("Time") && !usedTags.contains(entity.getTag()))
          .forEach(
              entity -> {
                final String term = entity.getTerm();
                if (StringUtils.isBlank(term)) {
                  return;
                }

                final String type = preProcessType(entity.getType());

                results.add(
                    new WordErrorWithPosition(
                        term,
                        type,
                        new BratPosition(entity.getStart(), entity.getEnd()),
                        annotation,
                        entity.getTag()));
              });
    }

    log.info("get potential isolated entity error list: {}", results.size());
    return postProcess(results, this.batchSize);
  }
}
