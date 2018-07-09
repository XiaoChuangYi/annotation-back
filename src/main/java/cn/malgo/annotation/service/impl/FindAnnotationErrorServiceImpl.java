package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.AnnotationWithPosition;
import cn.malgo.annotation.dto.WordTypeCount;
import cn.malgo.annotation.entity.AnnotationFixLog;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.service.FindAnnotationErrorService;
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

  private String getAnnotationFixLogKey(WordErrorWithPosition error) {
    return error.getAnnotation().getId()
        + "-"
        + error.getPosition().getStart()
        + "-"
        + error.getPosition().getEnd();
  }

  /** filter errors through {@link AnnotationFixLog} database */
  private Stream<WordErrorWithPosition> filterErrors(List<WordErrorWithPosition> errors) {
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
    switch (errorType) {
      case NEW_WORD:
        return findWordErrors(annotations);

      case ENTITY_MULTIPLE_TYPE:
        return findRelationErrors(annotations);
    }

    throw new IllegalArgumentException("invalid-error-type");
  }

  /**
   * 新词或旧词新意错误分析
   *
   * @param annotations 标注列表
   * @return 可能错误的词语
   */
  private List<AlgorithmAnnotationWordError> findWordErrors(List<Annotation> annotations) {
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

  /** 判断同一个entity是否都是同一个类型的 */
  private boolean isEntitiesDifferentType(final List<WordErrorWithPosition> positions) {
    return !positions
        .stream()
        .allMatch(position -> StringUtils.equals(position.getType(), positions.get(0).getType()));
  }

  private List<AlgorithmAnnotationWordError> findRelationErrors(
      final List<Annotation> annotations) {
    log.info("start finding relation errors");

    final Map<String, List<WordErrorWithPosition>> wordLists = new HashMap<>();

    for (Annotation annotation : annotations) {
      for (Entity entity : annotation.getDocument().getEntities()) {
        final String term = preProcessTerm(entity.getTerm());
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

  @lombok.Value
  static class WordErrorWithPosition implements AnnotationWithPosition {
    private final String term;
    private final String type;
    private final BratPosition position;
    private final Annotation annotation;
  }
}
