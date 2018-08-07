package cn.malgo.annotation.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum AnnotationEvaluateStateEnum {
  TOTAL(Arrays.asList(AnnotationTaskState.values()), Arrays.asList(AnnotationStateEnum.values())),
  ANNOTATED(
      Arrays.asList(AnnotationTaskState.FINISHED, AnnotationTaskState.ANNOTATED),
      Arrays.asList(AnnotationStateEnum.SUBMITTED)),
  REST(
      Arrays.asList(AnnotationTaskState.CREATED, AnnotationTaskState.DOING),
      Arrays.asList(AnnotationStateEnum.PRE_ANNOTATION, AnnotationStateEnum.ANNOTATION_PROCESSING));

  private final Set<AnnotationTaskState> blockStates;
  private final Set<AnnotationStateEnum> annotationStates;

  AnnotationEvaluateStateEnum(
      List<AnnotationTaskState> blockStates, List<AnnotationStateEnum> annotationStates) {
    this.blockStates = new HashSet<>(blockStates);
    this.annotationStates = new HashSet<>(annotationStates);
  }

  public Set<AnnotationTaskState> getBlockStates() {
    return blockStates;
  }

  public Set<AnnotationStateEnum> getAnnotationStates() {
    return annotationStates;
  }
}
