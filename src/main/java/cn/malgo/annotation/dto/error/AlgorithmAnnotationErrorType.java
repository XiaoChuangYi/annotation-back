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
public final class AlgorithmAnnotationErrorType {
  @NonNull private String type;
  private List<AlgorithmAnnotationErrorContext> context = new ArrayList<>();

  public void addAnnotation(
      final Annotation annotation, final BratPosition position, final Object info) {
    AlgorithmAnnotationErrorContext target = null;
    for (AlgorithmAnnotationErrorContext errorContext : this.context) {
      if (annotation.getId() == errorContext.getAnnotation().getId()) {
        target = errorContext;
        break;
      }
    }

    if (target == null) {
      target = new AlgorithmAnnotationErrorContext(annotation);
      this.context.add(target);
    }

    target.getIndex().add(Pair.of(position, info));
  }
}
