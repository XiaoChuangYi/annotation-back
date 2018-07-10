package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.AnnotationWithPosition;
import cn.malgo.annotation.dto.WordTypeCount;
import cn.malgo.annotation.entity.AnnotationFixLog;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.service.FindAnnotationErrorService;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.annotation.utils.entity.RelationEntity;
import cn.malgo.common.StringUtilsExt;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FindAnnotationErrorServiceImpl implements FindAnnotationErrorService {
  private static final Pattern[] IGNORE_WORD_PATTERNS =
      new Pattern[] {
        // numbers
        Pattern.compile("^-?\\d+(\\.\\d+)?$"),
        // blank
        Pattern.compile("^\\s+$"),
        // 疾病编码
        Pattern.compile("m\\d+\\s*/\\s*\\d+", Pattern.CASE_INSENSITIVE)
      };

  private static final Pair[] IGNORE_WORD_TYPES =
      new Pair[] {
        Pair.of("&", "Token"),
        Pair.of(";", "Token"),
        Pair.of("，", "Token"),
        Pair.of("。", "Token"),
        Pair.of("！", "Token"),
        Pair.of("!", "Token"),
        Pair.of("?", "Token"),
        Pair.of("？", "Token"),
      };

  private final AnnotationFixLogRepository annotationFixLogRepository;
  private final int batchSize;
  private Map<String, Map<String, WordTypeCount>> staticWordsDict;

  public FindAnnotationErrorServiceImpl(
      final AnnotationFixLogRepository annotationFixLogRepository,
      @Value("${malgo.annotation.fix-log-batch-size}") final int batchSize) {
    this.annotationFixLogRepository = annotationFixLogRepository;
    this.batchSize = batchSize;
  }

  @PostConstruct
  private void init() throws IOException {
    final JSONObject obj =
        JSONObject.parseObject(
            IOUtils.toString(
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("static-words.json")));

    staticWordsDict = new HashMap<>();
    for (String term : obj.keySet()) {
      staticWordsDict.put(term, new HashMap<>());
      for (String type : obj.getJSONObject(term).keySet()) {
        final JSONObject typeObj = obj.getJSONObject(term).getJSONObject(type);
        staticWordsDict
            .get(term)
            .put(
                type,
                new WordTypeCount(
                    type, typeObj.getIntValue("count"), typeObj.getIntValue("concept_id")));
      }
    }

    for (Pair ignoreWordType : IGNORE_WORD_TYPES) {
      if (!staticWordsDict.containsKey(ignoreWordType.getLeft())) {
        staticWordsDict.put(
            (String) ignoreWordType.getLeft(),
            Collections.singletonMap(
                (String) ignoreWordType.getRight(),
                new WordTypeCount((String) ignoreWordType.getRight(), 0, 0)));
      }
    }
  }

  private String preProcessType(String type) {
    String result = type;

    if (result.toLowerCase().endsWith("-unconfirmed")) {
      result = result.substring(0, result.length() - "-unconfirmed".length());
    }

    if (StringUtils.equalsAny(result.toLowerCase(), "logic", "blank", "unknown")) {
      result = "Token";
    }

    return result;
  }

  private String preProcessTerm(String term) {
    final String result = StringUtilsExt.preProcessString(term, true);
    for (Pattern pattern : IGNORE_WORD_PATTERNS) {
      if (pattern.matcher(term).matches()) {
        return null;
      }
    }
    return result;
  }

  private <T extends AnnotationWithPosition> String getAnnotationFixLogKey(T error) {
    return error.getAnnotation().getId()
        + "-"
        + error.getPosition().getStart()
        + "-"
        + error.getPosition().getEnd();
  }

  /** filter errors through {@link AnnotationFixLog} database */
  private <T extends AnnotationWithPosition> Stream<T> filterErrors(List<T> errors) {
    final Set<String> fixLogs =
        annotationFixLogRepository
            .findAllFixedLogs(errors)
            .stream()
            .map(AnnotationFixLog::getUniqueKey)
            .collect(Collectors.toSet());

    return errors.stream().filter(error -> !fixLogs.contains(getAnnotationFixLogKey(error)));
  }

  private AlgorithmAnnotationWordError mapToWordError(
      Map.Entry<String, List<WordErrorWithPosition>> entry) {
    final AlgorithmAnnotationWordError wordError =
        new AlgorithmAnnotationWordError(
            entry.getKey(),
            new ArrayList<>(
                staticWordsDict.containsKey(entry.getKey())
                    ? staticWordsDict.get(entry.getKey()).values()
                    : Collections.emptyList()));

    entry
        .getValue()
        .forEach(
            pair -> wordError.addError(pair.getAnnotation(), pair.getType(), pair.getPosition()));

    return wordError;
  }

  @Override
  public List<AlgorithmAnnotationWordError> findErrors(
      final AnnotationErrorEnum errorType, final List<Annotation> annotations) {
    List<WordErrorWithPosition> results;

    switch (errorType) {
      case NEW_WORD:
        results = findWordErrors(annotations);
        break;

      case ENTITY_MULTIPLE_TYPE:
        results = findEntityWithMultipleTypes(annotations);
        break;

      case ISOLATED_ENTITY:
        results = findIsolatedEntityErrors(annotations);
        break;

      case ENTITY_CONSISTENCY:
        results = findEntityConsistencyErrors(annotations);
        break;

      default:
        throw new IllegalArgumentException("invalid-error-type");
    }

    return Lists.partition(results, batchSize)
        .stream()
        // 过滤已经被处理过的错误
        .flatMap(this::filterErrors)
        // group by word
        .collect(Collectors.groupingBy(WordErrorWithPosition::getTerm))
        .entrySet()
        .stream()
        // 变成最终的数据结构
        .map(this::mapToWordError)
        .collect(Collectors.toList());
  }

  /**
   * 新词或旧词新意错误分析
   *
   * @param annotations 标注列表
   * @return 可能错误的词语
   */
  private List<WordErrorWithPosition> findWordErrors(List<Annotation> annotations) {
    log.info("start finding word errors");
    final List<WordErrorWithPosition> results = new ArrayList<>();

    for (Annotation annotation : annotations) {
      for (Entity entity : annotation.getDocument().getEntities()) {
        final String term = preProcessTerm(entity.getTerm());
        if (StringUtils.isBlank(term)) {
          continue;
        }

        final String type = preProcessType(entity.getType());

        if (!staticWordsDict.containsKey(term) || !staticWordsDict.get(term).containsKey(type)) {
          // 新词或者旧词新义
          results.add(
              new WordErrorWithPosition(
                  term, type, new BratPosition(entity.getStart(), entity.getEnd()), annotation));
        }
      }
    }

    log.info("get potential word error list: {}", results.size());
    return results;
  }

  /** 判断同一个entity是否都是同一个类型的 */
  private boolean isEntitiesDifferentType(final List<WordErrorWithPosition> positions) {
    return !positions
        .stream()
        .allMatch(position -> StringUtils.equals(position.getType(), positions.get(0).getType()));
  }

  private List<WordErrorWithPosition> findEntityWithMultipleTypes(
      final List<Annotation> annotations) {
    log.info("start finding relation errors");

    final Map<String, List<WordErrorWithPosition>> wordLists = new HashMap<>();

    for (Annotation annotation : annotations) {
      for (Entity entity : annotation.getDocument().getEntities()) {
        final String term = entity.getTerm();
        if (StringUtils.isBlank(term)) {
          continue;
        }

        final String type = preProcessType(entity.getType());

        wordLists
            .computeIfAbsent(term, t -> new ArrayList<>())
            .add(
                new WordErrorWithPosition(
                    term, type, new BratPosition(entity.getStart(), entity.getEnd()), annotation));
      }
    }

    final List<WordErrorWithPosition> results =
        wordLists
            .values()
            .stream()
            .filter(this::isEntitiesDifferentType)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    log.info("get potential relation error list: {}", results.size());
    return results;
  }

  private List<WordErrorWithPosition> findIsolatedEntityErrors(final List<Annotation> annotations) {
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
          .filter(entity -> !usedTags.contains(entity.getTag()))
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
                        annotation));
              });
    }

    log.info("get potential isolated entity error list: {}", results.size());
    return results;
  }

  private Stream<EntityListWithPosition> getTermEntities(
      final Annotation annotation, final String targetTerm) {
    final AnnotationDocument document = annotation.getDocument();

    if (!StringUtils.contains(document.getText(), targetTerm)) {
      return Stream.empty();
    }

    final List<EntityListWithPosition> results = new ArrayList<>();

    int index = -1;
    while ((index = document.getText().indexOf(targetTerm, index)) != -1) {
      final int start = index;
      final int end = index + targetTerm.length();
      final BratPosition position = new BratPosition(start, end);

      if (document.getEntities().stream().anyMatch(entity -> entity.intersectWith(start, end))) {
        index = end;
        continue;
      }

      // 找到所有在这个文本范围内的entity
      final List<Entity> entities = document.getEntitiesInside(position);
      final Map<String, Entity> entityMap =
          entities.stream().collect(Collectors.toMap(Entity::getTag, entity -> entity));

      // 找到所有tag以及和这些tag有关的外部关联关系
      final List<RelationEntity> relations = document.getRelationsOutsideToInside(entities);

      if (relations.size() == 0) {
        // 如果没有外部关联，则表示这是一个合法的对应子图
        results.add(new EntityListWithPosition(position, annotation, entities));
      } else {
        final RelationEntity firstRelation = relations.get(0);
        final Entity targetEntity =
            entityMap.get(
                entityMap.containsKey(firstRelation.getSourceTag())
                    ? firstRelation.getSourceTag()
                    : firstRelation.getTargetTag());
        if (relations
            .stream()
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
          results.add(new EntityListWithPosition(position, annotation, entities));
        }
      }

      index = end;
    }

    return results.stream();
  }

  private List<WordErrorWithPosition> findEntityConsistencyErrors(
      final List<Annotation> annotations) {
    //    final Comparator<String> comparator = (lhs, rhs) -> rhs.length() - lhs.length();
    final Comparator<String> comparator = (lhs, rhs) -> lhs.length() - rhs.length();

    final List<String> terms =
        annotations
            .stream()
            .flatMap(
                annotation -> annotation.getDocument().getEntities().stream().map(Entity::getTerm))
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toSet())
            .stream()
            .sorted(comparator)
            .collect(Collectors.toList());

    for (final String term : terms) {
      final List<EntityListWithPosition> entityLists =
          annotations
              .stream()
              .flatMap(annotation -> this.getTermEntities(annotation, term))
              .collect(Collectors.toList());

      if (!entityLists.stream().allMatch(entityList -> entityList.getEntities().size() == 1)) {
        // 如果找到的entityList中存在不是整个子串的，则表示有不一致性
        final List<EntityListWithPosition> filtered =
            filterErrors(entityLists).collect(Collectors.toList());
        if (!filtered.stream().allMatch(entityList -> entityList.getEntities().size() == 1)) {
          return filtered
              .stream()
              .map(
                  entityList ->
                      new WordErrorWithPosition(
                          term,
                          entityList.getEntities().size() == 1 ? "单一实体" : "子图",
                          entityList.getPosition(),
                          entityList.getAnnotation()))
              .collect(Collectors.toList());
        }
      }
    }

    return Collections.emptyList();
  }

  @lombok.Value
  static class WordErrorWithPosition implements AnnotationWithPosition {
    private final String term;
    private final String type;
    private final BratPosition position;
    private final Annotation annotation;
  }

  @lombok.Value
  static class EntityListWithPosition implements AnnotationWithPosition {
    private final BratPosition position;
    private final Annotation annotation;
    private final List<Entity> entities;
  }
}
