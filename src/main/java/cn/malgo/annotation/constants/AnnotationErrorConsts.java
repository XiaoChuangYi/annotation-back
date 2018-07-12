package cn.malgo.annotation.constants;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationErrorConsts {
  public static final List<Pair<String, String>> IGNORE_WORD_TYPES =
      Arrays.asList(
          Pair.of("&", "Token"),
          Pair.of(";", "Token"),
          Pair.of("，", "Token"),
          Pair.of("。", "Token"),
          Pair.of("！", "Token"),
          Pair.of("!", "Token"),
          Pair.of("?", "Token"),
          Pair.of("？", "Token"));

  public static final Set<String> IGNORE_WORDS =
      IGNORE_WORD_TYPES.stream().map(Pair::getLeft).collect(Collectors.toSet());
}
