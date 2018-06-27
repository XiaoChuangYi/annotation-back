package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.AnnotationWithPosition;
import cn.malgo.annotation.dto.WordTypeCount;
import cn.malgo.annotation.entity.AnnotationFixLog;
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

  private String getAnnotationFixLogKey(AnnotationWithPosition error) {
    return error.getAnnotationId() + "-" + error.getStart() + "-" + error.getEnd();
  }

  private AlgorithmAnnotationWordError mapToWordError(
      Map.Entry<String, List<AnnotationWordWithPosition>> entry) {
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
            pair ->
                wordError.addError(pair.getAnnotation(), pair.getType(), pair.getBratPosition()));

    return wordError;
  }

  @Override
  public <T extends AnnotationWithPosition> Stream<T> filterErrors(List<T> errors) {
    return Lists.partition(errors, batchSize)
        .stream()
        // 过滤已经被处理过的错误
        .flatMap(
            (batchErrors) -> {
              final Set<String> fixLogs =
                  annotationFixLogRepository
                      .findAllFixedLogs(batchErrors)
                      .stream()
                      .map(AnnotationFixLog::getUniqueKey)
                      .collect(Collectors.toSet());

              return batchErrors
                  .stream()
                  .filter(error -> !fixLogs.contains(getAnnotationFixLogKey(error)));
            });
  }

  @Override
  public List<AlgorithmAnnotationWordError> findErrors(List<Annotation> annotations) {
    log.info("start find errors");
    final List<AnnotationWordWithPosition> results = new ArrayList<>();

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
              new AnnotationWordWithPosition(
                  annotation, new BratPosition(entity.getStart(), entity.getEnd()), term, type));
        }
      }
    }

    log.info("get potential error list: {}", results.size());
    return this.filterErrors(results)
        // group by word
        .collect(Collectors.groupingBy(AnnotationWordWithPosition::getTerm))
        .entrySet()
        .stream()
        // 变成最终的数据结构
        .map(this::mapToWordError)
        .collect(Collectors.toList());
  }

  @lombok.Value
  static class AnnotationWordWithPosition implements AnnotationWithPosition {
    private final Annotation annotation;
    private final BratPosition bratPosition;
    private final String term;
    private final String type;

    @Override
    public int getAnnotationId() {
      return annotation.getId();
    }

    @Override
    public int getStart() {
      return bratPosition.getStart();
    }

    @Override
    public int getEnd() {
      return bratPosition.getEnd();
    }
  }
}
