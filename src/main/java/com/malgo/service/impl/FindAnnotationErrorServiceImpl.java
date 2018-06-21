package com.malgo.service.impl;

import cn.malgo.common.StringUtilsExt;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.malgo.dao.AnnotationFixLogRepository;
import com.malgo.dto.Annotation;
import com.malgo.dto.WordTypeCount;
import com.malgo.entity.AnnotationFixLog;
import com.malgo.service.FindAnnotationErrorService;
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

  private String getAnnotationFixLogKey(
      Pair<Pair<Annotation, BratPosition>, Pair<String, String>> error) {
    return error.getLeft().getLeft().getId()
        + "-"
        + error.getLeft().getRight().getStart()
        + "-"
        + error.getLeft().getRight().getEnd();
  }

  /** filter errors through {@link AnnotationFixLog} database */
  private Stream<Pair<Pair<Annotation, BratPosition>, Pair<String, String>>> filterErrors(
      List<Pair<Pair<Annotation, BratPosition>, Pair<String, String>>> errors) {
    final Set<String> fixLogs =
        annotationFixLogRepository
            .findAllFixedLogs(errors.stream().map(Pair::getLeft).collect(Collectors.toList()))
            .stream()
            .map(AnnotationFixLog::getUniqueKey)
            .collect(Collectors.toSet());

    return errors.stream().filter(error -> !fixLogs.contains(getAnnotationFixLogKey(error)));
  }

  private AlgorithmAnnotationWordError mapToWordError(
      Map.Entry<String, List<Pair<Pair<Annotation, BratPosition>, Pair<String, String>>>> entry) {
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
                wordError.addError(
                    pair.getLeft().getLeft(),
                    pair.getRight().getRight(),
                    pair.getLeft().getRight()));

    return wordError;
  }

  @Override
  public List<AlgorithmAnnotationWordError> findErrors(List<Annotation> annotations) {
    final List<Pair<Pair<Annotation, BratPosition>, Pair<String, String>>> results =
        new ArrayList<>();

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
              Pair.of(
                  Pair.of(annotation, new BratPosition(entity.getStart(), entity.getEnd())),
                  Pair.of(term, type)));
        }
      }
    }

    return Lists.partition(results, batchSize)
        .stream()
        // 过滤已经被处理过的错误
        .flatMap(this::filterErrors)
        // group by word
        .collect(Collectors.groupingBy(result -> result.getRight().getLeft()))
        .entrySet()
        .stream()
        // 变成最终的数据结构
        .map(this::mapToWordError)
        .collect(Collectors.toList());
  }
}
