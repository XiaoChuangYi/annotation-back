package com.malgo.service.impl;

import cn.malgo.common.StringUtilsExt;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import com.alibaba.fastjson.JSONObject;
import com.malgo.dto.Annotation;
import com.malgo.service.FindAnnotationErrorService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

  private JSONObject staticWordsDict;

  @PostConstruct
  private void init() throws IOException {
    staticWordsDict =
        JSONObject.parseObject(
            IOUtils.toString(
                Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream("static-words.json")));
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

  @Override
  public List<AlgorithmAnnotationWordError> findErrors(List<Annotation> annotations) {
    final Map<String, AlgorithmAnnotationWordError> result = new HashMap<>();

    for (Annotation annotation : annotations) {
      for (Entity entity : annotation.getDocument().getEntities()) {
        final String term = preProcessTerm(entity.getTerm());
        if (term == null) {
          continue;
        }

        final String type = preProcessType(entity.getType());

        if (!staticWordsDict.containsKey(term)
            || !staticWordsDict.getJSONObject(term).containsKey(type)) {
          result
              .computeIfAbsent(term, (t) -> new AlgorithmAnnotationWordError(term))
              .addError(annotation, type, new BratPosition(entity.getStart(), entity.getEnd()));
        }
      }
    }

    return result.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
  }
}
