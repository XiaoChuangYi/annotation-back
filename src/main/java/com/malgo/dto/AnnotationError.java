package com.malgo.dto;

import com.malgo.service.FindAnnotationErrorService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AnnotationError {
  private String type;
  private List<AnnotationErrorContext> annotations;

  public AnnotationError(FindAnnotationErrorService.AlgorithmAnnotationErrorType typeContext) {
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
                        .map(index -> new AnnotationErrorContext(context, index)))
            .collect(Collectors.toList());
  }
}
