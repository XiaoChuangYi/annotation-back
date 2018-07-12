package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.AnnotationWithPosition;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.entity.AnnotationFixLog;
import cn.malgo.annotation.service.AnnotationErrorProvider;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseErrorProvider implements AnnotationErrorProvider {
  private final AnnotationFixLogRepository annotationFixLogRepository;

  public BaseErrorProvider(final AnnotationFixLogRepository annotationFixLogRepository) {
    this.annotationFixLogRepository = annotationFixLogRepository;
  }

  String preProcessType(String type) {
    String result = type;

    if (result.toLowerCase().endsWith("-unconfirmed")) {
      result = result.substring(0, result.length() - "-unconfirmed".length());
    }

    if (StringUtils.equalsAny(result.toLowerCase(), "logic", "blank", "unknown")) {
      result = "Token";
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
  <T extends AnnotationWithPosition> Stream<T> filterErrors(List<T> errors) {
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
    final AlgorithmAnnotationWordError wordError = getWordError(entry.getKey());

    entry
        .getValue()
        .forEach(
            pair ->
                wordError.addError(
                    pair.getAnnotation(), pair.getType(), pair.getPosition(), pair.getInfo()));

    return wordError;
  }

  protected AlgorithmAnnotationWordError getWordError(final String word) {
    return new AlgorithmAnnotationWordError(word, Collections.emptyList());
  }

  List<AlgorithmAnnotationWordError> postProcess(
      final List<WordErrorWithPosition> results, final int batchSize) {
    final Stream<WordErrorWithPosition> filteredResults =
        batchSize != 0
            ? Lists.partition(results, batchSize)
                .stream()
                // 过滤已经被处理过的错误
                .flatMap(this::filterErrors)
            : results.stream();

    return filteredResults
        // group by word
        .collect(Collectors.groupingBy(WordErrorWithPosition::getTerm))
        .entrySet()
        .stream()
        // 变成最终的数据结构
        .map(this::mapToWordError)
        .collect(Collectors.toList());
  }
}
