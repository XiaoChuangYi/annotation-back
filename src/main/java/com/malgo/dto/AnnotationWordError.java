package com.malgo.dto;

import com.malgo.service.FindAnnotationErrorService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AnnotationWordError {
  private String word;
  private List<AnnotationError> errors;

  public AnnotationWordError(final FindAnnotationErrorService.AlgorithmAnnotationWordError error) {
    this.word = error.getWord();
    this.errors =
        error.getTypeContext().stream().map(AnnotationError::new).collect(Collectors.toList());
  }
}
