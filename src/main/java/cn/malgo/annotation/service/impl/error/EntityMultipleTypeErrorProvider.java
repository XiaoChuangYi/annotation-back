package cn.malgo.annotation.service.impl.error;

import static cn.malgo.annotation.constants.AnnotationErrorConsts.IGNORE_WORDS;

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
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EntityMultipleTypeErrorProvider extends BaseErrorProvider {

  private final int batchSize;

  public EntityMultipleTypeErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository,
      @Value("${malgo.annotation.fix-log-batch-size}") final int batchSize) {
    super(annotationFixLogRepository);

    this.batchSize = batchSize;
  }

  @Override
  public List<AnnotationErrorEnum> getErrorEnums() {
    return Arrays.asList(
        AnnotationErrorEnum.ENTITY_MULTIPLE_TYPE, AnnotationErrorEnum.DISEASE_MULTIPLE_TYPE);
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
                    type.replace("-and", ""),
                    new BratPosition(entity.getStart(), entity.getEnd()),
                    annotation,
                    entity.getTag()));
      }
    }

    log.info("all entities processed, word number: {}", wordLists.size());

    //    final Map<Long, BitSet> flagMap =
    //        annotations
    //            .parallelStream()
    //            .collect(
    //                Collectors.toMap(
    //                    Annotation::getId,
    //                    annotation -> {
    //                      final BitSet hasEntityFlag =
    //                          new BitSet(annotation.getDocument().getText().length());
    //                      for (Entity entity : annotation.getDocument().getEntities()) {
    //                        hasEntityFlag.set(entity.getStart(), entity.getEnd(), true);
    //                      }
    //                      return hasEntityFlag;
    //                    }));
    //
    //    log.info("entity flag map generated");
    //
    //    wordLists
    //        .entrySet()
    //        .parallelStream()
    //        .forEach(
    //            entry ->
    //                entry
    //                    .getValue()
    //                    .addAll(getNotAnnotatedPositions(entry.getKey(), annotations, flagMap)));
    //
    //    log.info("all not annotated positions processed");

    final List<List<WordErrorWithPosition>> differentEntities =
        wordLists
            .values()
            .parallelStream()
            .filter(this::isEntitiesDifferentType)
            .collect(Collectors.toList());

    log.info("find all different entities, size: {}", differentEntities.size());

    final List<List<WordErrorWithPosition>> results =
        Lists.partition(
                differentEntities.stream().flatMap(Collection::stream).collect(Collectors.toList()),
                this.batchSize)
            .parallelStream()
            // 过滤已经被处理过的错误
            .flatMap(this::filterErrors)
            .collect(Collectors.groupingBy(WordErrorWithPosition::getTerm))
            .values()
            .stream()
            .filter(this::isEntitiesDifferentType)
            .collect(Collectors.toList());

    log.info("find all different entities after filter fixed logs, size: {}", results.size());

    return postProcess(
        results.stream().flatMap(Collection::stream).collect(Collectors.toList()), 0);
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
            getNewEntities(oldDoc, start, end, fixEntity, annotation));

    annotation.setAnnotation(AnnotationDocumentManipulator.toBratAnnotations(newDoc));

    return newDoc
        .getEntities()
        .stream()
        .filter(entity -> entity.getStart() == start && entity.getEnd() == end)
        .collect(Collectors.toList());
  }

  private List<Entity> getNewEntities(
      final AnnotationDocument oldDoc,
      final int start,
      final int end,
      final FixAnnotationEntity fixEntity,
      final Annotation annotation) {
    if (oldDoc
        .getEntities()
        .stream()
        .noneMatch(oldEntity -> oldEntity.getStart() == start && oldEntity.getEnd() == end)) {
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
      oldDoc
          .getEntities()
          .add(
              new Entity(
                  oldDoc.getNewEntityTag(oldDoc.getEntities()),
                  start,
                  end,
                  fixEntity.getType(),
                  fixEntity.getTerm()));
      return oldDoc.getEntities();
    } else {
      return oldDoc
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
          .collect(Collectors.toList());
    }
  }

  /** 判断同一个entity是否都是同一个类型的 */
  private boolean isEntitiesDifferentType(final List<WordErrorWithPosition> positions) {
    return !positions
        .parallelStream()
        .allMatch(position -> StringUtils.equals(position.getType(), positions.get(0).getType()));
  }

  private List<WordErrorWithPosition> getNotAnnotatedPositions(
      final String term,
      final List<Annotation> annotations,
      final Map<Long, BitSet> entityFlagMap) {
    return annotations
        .parallelStream()
        .flatMap(
            annotation ->
                getNotAnnotatedPositions(annotation, term, entityFlagMap.get(annotation.getId())))
        .collect(Collectors.toList());
  }

  private Stream<WordErrorWithPosition> getNotAnnotatedPositions(
      final Annotation annotation, final String targetTerm, final BitSet hasEntityFlag) {
    final List<WordErrorWithPosition> results = new ArrayList<>();
    final String text = annotation.getDocument().getText();

    int index = -1;
    while ((index = text.indexOf(targetTerm, index)) != -1) {
      final int start = index;
      final int end = index + targetTerm.length();

      // 当前位置不存在entity
      if (IntStream.range(start, end).noneMatch(hasEntityFlag::get)) {
        results.add(
            new WordErrorWithPosition(
                targetTerm, "未标注实体", new BratPosition(start, end), annotation, null));
      }

      index = end;
    }
    return results.stream();
  }
}
