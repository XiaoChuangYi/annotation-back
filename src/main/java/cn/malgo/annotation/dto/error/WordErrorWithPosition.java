package cn.malgo.annotation.dto.error;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.Value;

@Value
public class WordErrorWithPosition implements AnnotationWithPosition {
  private final String term;
  private final String type;
  private final BratPosition position;
  private final Annotation annotation;
  private final Object info;
}
