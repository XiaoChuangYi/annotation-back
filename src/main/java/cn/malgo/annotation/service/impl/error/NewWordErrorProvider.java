package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.WordTypeCount;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.FixAnnotationEntity;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.common.StringUtilsExt;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import com.alibaba.fastjson.JSONObject;
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

import static cn.malgo.annotation.constants.AnnotationErrorConsts.IGNORE_WORD_TYPES;

/** 新词或旧词新意错误分析 */
@Service
@Slf4j
public class NewWordErrorProvider extends BaseErrorProvider {
  private static final Pattern[] IGNORE_WORD_PATTERNS =
      new Pattern[] {
        // numbers
        Pattern.compile("^-?\\d+(\\.\\d+)?$"),
        // blank
        Pattern.compile("^\\s+$"),
        // 疾病编码
        Pattern.compile("m\\d+\\s*/\\s*\\d+", Pattern.CASE_INSENSITIVE)
      };

  private final int batchSize;
  private Map<String, Map<String, WordTypeCount>> staticWordsDict;

  public NewWordErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository,
      @Value("${malgo.annotation.fix-log-batch-size}") final int batchSize) {
    super(annotationFixLogRepository);

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

    for (final Pair<String, String> ignoreWordType : IGNORE_WORD_TYPES) {
      if (!staticWordsDict.containsKey(ignoreWordType.getLeft())) {
        staticWordsDict.put(
            ignoreWordType.getLeft(),
            Collections.singletonMap(
                ignoreWordType.getRight(), new WordTypeCount(ignoreWordType.getRight(), 0, 0)));
      }
    }
  }

  @Override
  public AnnotationErrorEnum getErrorEnum() {
    return AnnotationErrorEnum.NEW_WORD;
  }

  @Override
  public List<AlgorithmAnnotationWordError> find(final List<Annotation> annotations) {
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
    return postProcess(results, this.batchSize);
  }

  @Override
  public List<Entity> fix(
      final Annotation annotation,
      final int start,
      final int end,
      final List<FixAnnotationEntity> fixEntities) {
    final String targetText =
        String.join(
            "",
            fixEntities.stream().map(FixAnnotationEntity::getTerm).collect(Collectors.toList()));

    final String text = annotation.getDocument().getText();

    if (!targetText.contains(text.substring(start, end))) {
      // 修复的字符串合并以后必须至少包含原字符串
      // 例如将  摩擦伤 -> 摩擦   是不被允许的
      throw new InvalidInputException(
          "invalid-fix-annotation",
          String.format("\"%s\"必须包含\"%s\"", targetText, text.substring(start, end)));
    }

    // 从end - targetText.length()开始寻找
    int index = Math.max(0, end - targetText.length());
    while ((index = text.indexOf(targetText, index)) != -1) {
      if (index >= end) {
        break;
      }

      if (index <= start && index + targetText.length() >= end) {
        // 找到了词条
        final int realStart = index;
        final int realEnd = index + targetText.length();

        annotation.getDocument().getEntities().sort(Comparator.comparingInt(Entity::getStart));
        if (annotation
            .getDocument()
            .getEntities()
            .stream()
            .anyMatch(
                entity ->
                    (entity.getStart() < realStart && entity.getEnd() > realStart)
                        || (entity.getStart() < realEnd && entity.getEnd() > realEnd))) {
          // 如果存在已经标注的部分和想要修复的部分有交集但不完全包含，则失败
          // 例如，"abcdef"这句话，修复为"cde"，但是"bcd"被标记过，则失败
          throw new InvalidInputException("invalid-fix-annotation", "想要修复的部分和已经标注的集合不完全匹配");
        }

        final List<Entity> entities =
            annotation
                .getDocument()
                .getEntities()
                .stream()
                .filter(entity -> entity.getEnd() <= realStart || entity.getStart() >= realEnd)
                .collect(Collectors.toList());

        for (int i = 0; i < entities.size(); ++i) {
          entities.get(i).setTag("T" + (i + 1));
        }

        final int newEntities = entities.size();
        int now = realStart;
        for (FixAnnotationEntity entity : fixEntities) {
          entities.add(
              new Entity(
                  "T" + (entities.size() + 1),
                  now,
                  now + entity.getTerm().length(),
                  entity.getType(),
                  entity.getTerm()));
          now += entity.getTerm().length();
        }

        annotation.setAnnotation(
            AnnotationDocumentManipulator.toBratAnnotations(
                new AnnotationDocument(
                    annotation.getDocument().getText(), new ArrayList<>(), entities)));
        return entities.subList(newEntities, entities.size());
      }

      index += targetText.length();
    }

    throw new InvalidInputException(
        "invalid-fix-annotation",
        String.format("未在\"%s\"中找到开始小于%s，结束大于等于%s的字符串\"%s\"", text, start, end, targetText));
  }

  @Override
  protected AlgorithmAnnotationWordError getWordError(final String word) {
    return new AlgorithmAnnotationWordError(
        word,
        new ArrayList<>(
            staticWordsDict.containsKey(word)
                ? staticWordsDict.get(word).values()
                : Collections.emptyList()));
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
}
