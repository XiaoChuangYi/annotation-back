package cn.malgo.annotation.service.impl.error;

import static cn.malgo.annotation.constants.AnnotationErrorConsts.IGNORE_WORDS;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.FixAnnotationEntity;
import cn.malgo.annotation.dto.error.FixAnnotationErrorData;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.RelationEntity;
import cn.malgo.core.definition.brat.BratPosition;
import cn.malgo.service.exception.InvalidInputException;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AttributeEntityConsisErrorProvider extends BaseErrorProvider {

  private final int batchSize;
  private Set<Long> cachedAnnotationIds = new HashSet<>(100000);
  private Map<String, Set<Long>> cachedTerms = new HashMap<>(100000);

  public AttributeEntityConsisErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository,
      @Value("${malgo.annotation.fix-log-batch-size}") final int batchSize) {
    super(annotationFixLogRepository);

    this.batchSize = batchSize;
  }

  @Override
  public List<AnnotationErrorEnum> getErrorEnums() {
    return Arrays.asList(AnnotationErrorEnum.ATTRIBUTE_MULTIPLE_TYPE);
  }

  @Override
  public List<AlgorithmAnnotationWordError> find(final List<Annotation> annotations) {
    final Set<String> terms =
        annotations
            .parallelStream()
            .flatMap(
                annotation -> annotation.getDocument().getEntities().stream().map(Entity::getTerm))
            .filter(term -> StringUtils.isNotBlank(term) && !IGNORE_WORDS.contains(term))
            .collect(Collectors.toSet());

    log.info("get all terms size: {}", terms.size());

    cachedTerms.putAll(
        terms
            .parallelStream()
            .filter(term -> !cachedTerms.containsKey(term))
            .collect(
                Collectors.toMap(term -> term, term -> getTermAnnotations(term, annotations))));

    final List<Annotation> newAnnotations =
        annotations
            .parallelStream()
            .filter(ann -> !cachedAnnotationIds.contains(ann.getId()))
            .collect(Collectors.toList());

    cachedAnnotationIds.addAll(
        newAnnotations.parallelStream().map(Annotation::getId).collect(Collectors.toSet()));

    cachedTerms
        .entrySet()
        .parallelStream()
        .forEach(
            entry -> entry.getValue().addAll(getTermAnnotations(entry.getKey(), newAnnotations)));

    cachedTerms.keySet().removeIf(term -> !terms.contains(term));

    log.info("cached terms size: {}", cachedTerms.size());

    final Map<Long, Annotation> annotationMap =
        annotations.parallelStream().collect(Collectors.toMap(Annotation::getId, ann -> ann));

    final List<EntityConsistencyErrorProvider.EntityListWithPosition> entityListWithPosition =
        cachedTerms
            .entrySet()
            .parallelStream()
            .map(
                entry ->
                    entry
                        .getValue()
                        .parallelStream()
                        .flatMap(id -> this.getTermEntities(annotationMap.get(id), entry.getKey()))
                        .collect(Collectors.toList()))
            .filter(this::isEntityInconsistency)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    log.info("get all possible entities size: {}", entityListWithPosition.size());

    return postProcess(
        Lists.partition(entityListWithPosition, this.batchSize)
            .parallelStream()
            // 过滤已经被处理过的错误
            .flatMap(this::filterErrors)
            .collect(Collectors
                .groupingBy(EntityConsistencyErrorProvider.EntityListWithPosition::getTerm))
            .values()
            .parallelStream()
            .filter(this::isEntityInconsistency)
            .flatMap(Collection::stream)
            .map(
                entityList ->
                    new WordErrorWithPosition(
                        entityList.getTerm(),
                        entityList.getEntitySize() == 1 ? "单一实体" : "子图",
                        entityList.getPosition(),
                        entityList.getAnnotation(),
                        null))
            .collect(Collectors.toList()),
        0);
  }

  @Override
  public List<Entity> fix(
      final Annotation annotation,
      final int start,
      final int end,
      final FixAnnotationErrorData data) {
    log.info("fixing {}, position: ({}, {}), data: {}", annotation.getId(), start, end, data);

    final List<FixAnnotationEntity> entities = data.getEntities();
    final AnnotationDocument document = annotation.getDocument();
    final String text = document.getText();
    final String subString = text.substring(start, end);

    if (entities.stream().anyMatch(entity -> !StringUtils.contains(subString, entity.getTerm()))) {
      throw new InvalidInputException(
          "invalid-fix-entities", "存在不包含在\"" + text + "\"中的entity，请检查表格");
    }

    final int oldEntitySize =
        document
            .getEntities()
            .stream()
            .mapToInt(entity -> Integer.parseInt(entity.getTag().replace("T", "")))
            .max()
            .orElse(1);
    final List<Entity> createdEntities = new ArrayList<>();

    for (final FixAnnotationEntity entity : entities) {
      final int entityStart = text.indexOf(entity.getTerm(), start);

      if (entityStart == -1 || entityStart + entity.getTerm().length() > end) {
        throw new InvalidInputException(
            "invalid-fix-entities", "存在不包含在\"" + text + "\"中的entity，请检查表格");
      }

      createdEntities.add(
          new Entity(
              "T" + (oldEntitySize + createdEntities.size() + 1),
              entityStart,
              entityStart + entity.getTerm().length(),
              entity.getType(),
              entity.getTerm()));
    }

    final List<Entity> oldEntities =
        document.getEntitiesInside(new BratPosition(start, end), false);

    document.getEntities().removeAll(oldEntities);
    document.getEntities().addAll(createdEntities);

    annotation.setAnnotation(AnnotationDocumentManipulator.toBratAnnotations(document));
    return Collections.emptyList();
  }

  private Set<Long> getTermAnnotations(final String term, final List<Annotation> annotations) {
    return annotations
        .parallelStream()
        .filter(ann -> StringUtils.contains(ann.getDocument().getText(), term))
        .map(Annotation::getId)
        .collect(Collectors.toSet());
  }

  private boolean isEntityInconsistency(
      List<EntityConsistencyErrorProvider.EntityListWithPosition> entityLists) {
    final Set<Integer> sizes =
        entityLists
            .parallelStream()
            .map(EntityConsistencyErrorProvider.EntityListWithPosition::getEntitySize)
            .collect(Collectors.toSet());

    return sizes.contains(1) && sizes.size() != 1;
  }

  private Stream<EntityConsistencyErrorProvider.EntityListWithPosition> getTermEntities(
      final Annotation annotation, final String targetTerm) {
    final AnnotationDocument document = annotation.getDocument();

    if (document.getEntities().size() == 0) {
      return Stream.empty();
    }

    final List<EntityConsistencyErrorProvider.EntityListWithPosition> results = new ArrayList<>();

    int index = -1;
    while ((index = document.getText().indexOf(targetTerm, index)) != -1) {
      final int start = index;
      final int end = index + targetTerm.length();
      final BratPosition position = new BratPosition(start, end);

      if (document
          .getEntities()
          .parallelStream()
          .anyMatch(entity -> entity.intersectWith(start, end))) {
        index = end;
        continue;
      }

      // 找到所有在这个文本范围内的entity
      final List<Entity> entities = document.getEntitiesInside(position, false);
      if (entities.size() == 0) {
        index = end;
        continue;
      }

      final Map<String, Entity> entityMap =
          entities.parallelStream().collect(Collectors.toMap(Entity::getTag, entity -> entity));

      // 找到所有tag以及和这些tag有关的外部关联关系
      final List<RelationEntity> relations = document.getRelationsOutsideToInside(entities);

      if (relations.size() == 0) {
        // 如果没有外部关联，则表示这是一个合法的对应子图
        results.add(new EntityConsistencyErrorProvider.EntityListWithPosition(position, annotation,
            entities));
      } else {
        final RelationEntity firstRelation = relations.get(0);
        final Entity targetEntity =
            entityMap.get(
                entityMap.containsKey(firstRelation.getSourceTag())
                    ? firstRelation.getSourceTag()
                    : firstRelation.getTargetTag());
        if (relations
            .parallelStream()
            .allMatch(
                relation -> {
                  final Entity entity =
                      entityMap.get(
                          entityMap.containsKey(relation.getSourceTag())
                              ? relation.getSourceTag()
                              : relation.getTargetTag());
                  return entity.getStart() == targetEntity.getStart()
                      && entity.getEnd() == targetEntity.getEnd();
                })) {
          results.add(
              new EntityConsistencyErrorProvider.EntityListWithPosition(position, annotation,
                  entities));
        }
      }

      index = end;
    }

    return results.stream();
  }
}
