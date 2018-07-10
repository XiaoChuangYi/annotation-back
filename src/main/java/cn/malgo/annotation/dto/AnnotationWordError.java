package cn.malgo.annotation.dto;

import cn.malgo.annotation.service.FindAnnotationErrorService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class AnnotationWordError {
  @NonNull private String word;
  @NonNull private List<WordTypeCount> counts;
  @NonNull private List<AnnotationError> errors;

  public AnnotationWordError(final FindAnnotationErrorService.AlgorithmAnnotationWordError error) {
    this.word = error.getWord();
    this.counts = error.getCounts();
    this.errors =
        error
            .getTypeContext()
            .stream()
            .sorted(Comparator.comparingInt(lhs -> lhs.getContext().size()))
            .map(AnnotationError::new)
            .collect(Collectors.toList());
  }
}
