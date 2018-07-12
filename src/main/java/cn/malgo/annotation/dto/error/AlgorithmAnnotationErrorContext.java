package cn.malgo.annotation.dto.error;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@ToString
@Getter
public final class AlgorithmAnnotationErrorContext {
  @NonNull private Annotation annotation;
  private List<Pair<BratPosition, Object>> index = new ArrayList<>();
}
