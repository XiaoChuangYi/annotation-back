package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.FixAnnotationEntity;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.malgo.annotation.constants.AnnotationErrorConsts.IGNORE_WORDS;

@Slf4j
@Service
public class EntityMultipleTypeErrorProvider extends BaseErrorProvider {
  public EntityMultipleTypeErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository) {
    super(annotationFixLogRepository);
  }

  @Override
  public AnnotationErrorEnum getErrorEnum() {
    return AnnotationErrorEnum.ENTITY_MULTIPLE_TYPE;
  }

  @Override
  public List<AlgorithmAnnotationWordError> find(final List<Annotation> annotations) {
    log.info("start finding errors");

    final Map<String, List<WordErrorWithPosition>> wordLists = new HashMap<>();

    for (Annotation annotation : annotations) {
      for (Entity entity : annotation.getDocument().getEntities()) {
        final String term = entity.getTerm();
        if (StringUtils.isBlank(term) || IGNORE_WORDS.contains(term)) {
          continue;
        }

        final String type = preProcessType(entity.getType());

        wordLists
            .computeIfAbsent(term, t -> new ArrayList<>())
            .add(
                new WordErrorWithPosition(
                    term,
                    type,
                    new BratPosition(entity.getStart(), entity.getEnd()),
                    annotation,
                    null));
      }
    }

    wordLists.forEach(
        (term, words) ->
            words.addAll(
                annotations
                    .stream()
                    .flatMap(annotation -> getNotAnnotatedPositions(annotation, term))
                    .collect(Collectors.toList())));

    final List<WordErrorWithPosition> results =
        wordLists
            .values()
            .stream()
            .filter(this::isEntitiesDifferentType)
            .map(wordList -> this.filterErrors(wordList).collect(Collectors.toList()))
            .filter(this::isEntitiesDifferentType)
            .findFirst()
            .orElse(Collections.emptyList());

    log.info("get potential error list: {}", results.size());
    return postProcess(results, 0);
  }

  @Override
  public List<Entity> fix(
      final Annotation annotation,
      final int start,
      final int end,
      final List<FixAnnotationEntity> entities) {
    if (entities.size() != 1) {
      throw new IllegalArgumentException(
          "ENTITY_MULTIPLE_TYPE 修复只接受一个修复对象，实际得到: " + entities.size());
    }

    final FixAnnotationEntity fixEntity = entities.get(0);

    final AnnotationDocument oldDoc = annotation.getDocument();
    final AnnotationDocument newDoc =
        new AnnotationDocument(
            oldDoc.getText(),
            oldDoc.getRelationEntities(),
            oldDoc
                .getEntities()
                .stream()
                .map(
                    entity -> {
                      if (entity.getStart() != start || entity.getEnd() != end) {
                        return entity;
                      }

                      if (!StringUtils.equalsIgnoreCase(
                          oldDoc.getText().substring(start, end), fixEntity.getTerm())) {
                        throw new IllegalArgumentException(
                            "mismatch term: "
                                + fixEntity.getTerm()
                                + ", annotation id: "
                                + annotation.getId()
                                + ", start: "
                                + start
                                + ", end: "
                                + end);
                      }

                      return new Entity(
                          entity.getTag(), start, end, fixEntity.getType(), fixEntity.getTerm());
                    })
                .collect(Collectors.toList()));

    annotation.setAnnotation(AnnotationDocumentManipulator.toBratAnnotations(newDoc));

    return newDoc
        .getEntities()
        .stream()
        .filter(entity -> entity.getStart() == start && entity.getEnd() == end)
        .collect(Collectors.toList());
  }

  /** 判断同一个entity是否都是同一个类型的 */
  private boolean isEntitiesDifferentType(final List<WordErrorWithPosition> positions) {
    return !positions
        .stream()
        .allMatch(position -> StringUtils.equals(position.getType(), positions.get(0).getType()));
  }

  private Stream<WordErrorWithPosition> getNotAnnotatedPositions(
      final Annotation annotation, final String targetTerm) {
    final List<WordErrorWithPosition> results = new ArrayList<>();
    final String text = annotation.getDocument().getText();
    int index = -1;
    while ((index = text.indexOf(targetTerm, index)) != -1) {
      final int start = index;
      final int end = index + targetTerm.length();

      if (!annotation.getDocument().hasEntityBetweenPosition(start, end)) {
        results.add(
            new WordErrorWithPosition(
                targetTerm, "未标注实体", new BratPosition(start, end), annotation, null));
      }

      index = end;
    }
    return results.stream();
  }
}
