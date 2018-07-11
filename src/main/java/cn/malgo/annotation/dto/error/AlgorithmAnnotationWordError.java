package cn.malgo.annotation.dto.error;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.WordTypeCount;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@ToString
@Getter
public final class AlgorithmAnnotationWordError {
  @NonNull private final String word;
  @NonNull private final List<WordTypeCount> counts;
  private List<AlgorithmAnnotationErrorType> typeContext = new ArrayList<>();

  public void addError(Annotation annotation, String type, BratPosition position) {
    AlgorithmAnnotationErrorType target = null;
    for (AlgorithmAnnotationErrorType errorType : this.typeContext) {
      if (errorType.getType().equals(type)) {
        target = errorType;
        break;
      }
    }

    if (target == null) {
      target = new AlgorithmAnnotationErrorType(type);
      this.typeContext.add(target);
    }

    target.addAnnotation(annotation, position);
  }
}
