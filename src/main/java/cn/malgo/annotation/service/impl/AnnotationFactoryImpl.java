package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
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

    if (StringUtils.equalsAny(
        annotationCombine.getState(),
        AnnotationCombineStateEnum.preExamine.name(),
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
    public AnnotationTypeEnum getAnnotationType() {
      return AnnotationTypeEnum.getByValue(annotationCombine.getAnnotationType());
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

  //  static class AnnotationFinal extends BaseAnnotation {
  //    AnnotationFinal(final AnnotationCombine annotationCombine) {
  //      super(annotationCombine);
  //    }
  //
  //    @Override
  //    public String getAnnotation() {
  //      return annotationCombine.getFinalAnnotation();
  //    }
  //
  //    @Override
  //    public void setAnnotation(String annotation) {
  //      super.setAnnotation(annotation);
  //      annotationCombine.setFinalAnnotation(annotation);
  //    }
  //  }

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
