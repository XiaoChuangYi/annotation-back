package com.malgo.service.impl;

import com.malgo.dto.Annotation;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.service.AnnotationFactory;
import com.malgo.utils.AnnotationDocumentManipulator;
import com.malgo.utils.entity.AnnotationDocument;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class AnnotationFactoryImpl implements AnnotationFactory {
  @Override
  public Annotation create(final AnnotationCombine annotationCombine) {
    if (annotationCombine == null) {
      throw new NullPointerException("create annotation get null");
    }

    if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preExamine.name())) {
      return new AnnotationFinal(annotationCombine);
    }

    if (StringUtils.equalsAny(
        annotationCombine.getState(),
        AnnotationCombineStateEnum.examinePass.name(),
        AnnotationCombineStateEnum.errorPass.name())) {
      return new AnnotationReviewed(annotationCombine);
    }

    throw new IllegalArgumentException(
        "annotation factory get annotation with state: " + annotationCombine.getState());
  }

  abstract static class BaseAnnotation implements Annotation {
    @NonNull protected final AnnotationCombine annotationCombine;
    private AnnotationDocument document;

    BaseAnnotation(final AnnotationCombine annotationCombine) {
      this.annotationCombine = annotationCombine;
    }

    @Override
    public int getId() {
      return annotationCombine.getId();
    }

    @Override
    public void setAnnotation(String annotation) {
      document = null;
    }

    @Override
    public AnnotationDocument getDocument() {
      if (document == null) {
        document = new AnnotationDocument(annotationCombine.getTerm());
        AnnotationDocumentManipulator.parseBratAnnotation(getAnnotation(), document);
      }

      return document;
    }

    @Override
    public String toString() {
      return this.getClass() + "@" + annotationCombine.getId();
    }
  }

  static class AnnotationFinal extends BaseAnnotation {
    AnnotationFinal(final AnnotationCombine annotationCombine) {
      super(annotationCombine);
    }

    @Override
    public String getAnnotation() {
      return annotationCombine.getFinalAnnotation();
    }

    @Override
    public void setAnnotation(String annotation) {
      super.setAnnotation(annotation);
      annotationCombine.setFinalAnnotation(annotation);
    }
  }

  static class AnnotationReviewed extends BaseAnnotation {
    AnnotationReviewed(AnnotationCombine annotationCombine) {
      super(annotationCombine);
    }

    @Override
    public String getAnnotation() {
      return annotationCombine.getReviewedAnnotation();
    }

    @Override
    public void setAnnotation(String annotation) {
      super.setAnnotation(annotation);
      annotationCombine.setReviewedAnnotation(annotation);
    }
  }
}
