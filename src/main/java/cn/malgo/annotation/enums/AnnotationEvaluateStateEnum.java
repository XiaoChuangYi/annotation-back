package cn.malgo.annotation.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

  private final List<AnnotationTaskState> blockStates;
  private final List<AnnotationCombineStateEnum> annotationStates;

  AnnotationEvaluateStateEnum(
      List<AnnotationTaskState> blockStates, List<AnnotationCombineStateEnum> annotationStates) {
    this.blockStates = blockStates;
    this.annotationStates = annotationStates;
  }

  public List<AnnotationTaskState> getBlockStates() {
    return blockStates;
  }

  public List<AnnotationCombineStateEnum> getAnnotationStates() {
    return annotationStates;
  }
}
