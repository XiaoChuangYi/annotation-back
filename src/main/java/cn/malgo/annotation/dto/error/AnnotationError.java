package cn.malgo.annotation.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AnnotationError {
  private String type;
  private List<AnnotationErrorContext> annotations;

  public AnnotationError(AlgorithmAnnotationErrorType typeContext) {
    this.type = typeContext.getType();
    this.annotations =
        typeContext
            .getContext()
            .stream()
            .flatMap(
                context ->
                    context
                        .getIndex()
                        .stream()
                        .map(index -> new AnnotationErrorContext(context.getAnnotation(), index)))
            .collect(Collectors.toList());
  }
}
