package cn.malgo.annotation.service;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.WordTypeCount;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

public interface FindAnnotationErrorService {
  List<AlgorithmAnnotationWordError> findErrors(
      AnnotationErrorEnum errorType, List<Annotation> annotations);

  @RequiredArgsConstructor
  @ToString
  @Getter
  final class AlgorithmAnnotationWordError {
    @NonNull private final String word;
    @NonNull private final List<WordTypeCount> counts;
    private List<AlgorithmAnnotationErrorType> typeContext = new ArrayList<>();

    public void addError(Annotation annotation, String type, BratPosition position) {
      AlgorithmAnnotationErrorType target = null;
      for (AlgorithmAnnotationErrorType errorType : this.typeContext) {
        if (errorType.type.equals(type)) {
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

  @RequiredArgsConstructor
  @ToString
  @Getter
  final class AlgorithmAnnotationErrorType {
    @NonNull private String type;
    private List<AlgorithmAnnotationErrorContext> context = new ArrayList<>();

    public void addAnnotation(Annotation annotation, BratPosition position) {
      AlgorithmAnnotationErrorContext target = null;
      for (AlgorithmAnnotationErrorContext errorContext : this.context) {
        if (annotation.getId() == errorContext.annotation.getId()) {
          target = errorContext;
          break;
        }
      }

      if (target == null) {
        target = new AlgorithmAnnotationErrorContext(annotation);
        this.context.add(target);
      }

      target.index.add(position);
    }
  }

  @RequiredArgsConstructor
  @ToString
  @Getter
  final class AlgorithmAnnotationErrorContext {
    @NonNull private Annotation annotation;
    private List<BratPosition> index = new ArrayList<>();
  }
}
