package cn.malgo.annotation.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum AnnotationEvaluateStateEnum {
  TOTAL(
      Arrays.asList(AnnotationTaskState.values()),
      Arrays.asList(AnnotationCombineStateEnum.values())),
  ANNOTATED(
      Arrays.asList(AnnotationTaskState.FINISHED, AnnotationTaskState.ANNOTATED),
      Arrays.asList(
          AnnotationCombineStateEnum.preExamine,
          AnnotationCombineStateEnum.examinePass,
          AnnotationCombineStateEnum.errorPass)),
  REST(
      Arrays.asList(AnnotationTaskState.CREATED, AnnotationTaskState.DOING),
      Arrays.asList(
          AnnotationCombineStateEnum.preAnnotation,
          AnnotationCombineStateEnum.annotationProcessing)),
  ABANDON(
      Collections.emptyList(),
      Arrays.asList(
          AnnotationCombineStateEnum.abandon, AnnotationCombineStateEnum.innerAnnotation));

  private final Set<AnnotationTaskState> blockStates;
  private final Set<AnnotationCombineStateEnum> annotationStates;

  AnnotationEvaluateStateEnum(
      List<AnnotationTaskState> blockStates, List<AnnotationCombineStateEnum> annotationStates) {
    this.blockStates = new HashSet<>(blockStates);
    this.annotationStates = new HashSet<>(annotationStates);
  }

  public Set<AnnotationTaskState> getBlockStates() {
    return blockStates;
  }

  public Set<AnnotationCombineStateEnum> getAnnotationStates() {
    return annotationStates;
  }
}
