package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
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

    switch (annotationCombine.getStateEnum()) {
      case preAnnotation:
      case annotationProcessing:
      case unDistributed:
        return new AnnotationFinal(annotationCombine);

      case preExamine:
      case abandon:
      case innerAnnotation:
      case errorPass:
      case examinePass:
        return new AnnotationReviewed(annotationCombine);
    }

    throw new IllegalArgumentException("标注状态错误: " + annotationCombine.getState());
  }

  @Override
  public Annotation create(final AnnotationTaskBlock block) {
    if (block == null) {
      throw new NullPointerException("create annotation get null");
    }

    switch (block.getState()) {
      case ANNOTATED:
      case FINISHED:
        return new AnnotationBlock(block);
    }

    throw new IllegalArgumentException("标注状态错误: " + block.getState());
  }

  abstract static class BaseAnnotation implements Annotation {
    private AnnotationDocument document;

    protected abstract String getText();

    @Override
    public void setAnnotation(String annotation) {
      document = null;
    }

    @Override
    public AnnotationDocument getDocument() {
      if (document == null) {
        document = new AnnotationDocument(getText());
        AnnotationDocumentManipulator.parseBratAnnotation(getAnnotation(), document);
      }

      return document;
    }

    @Override
    public String toString() {
      return this.getClass() + "@" + getId();
    }
  }

  abstract static class BaseAnnotationCombine extends BaseAnnotation {
    @NonNull protected final AnnotationCombine annotationCombine;

    BaseAnnotationCombine(final AnnotationCombine annotationCombine) {
      this.annotationCombine = annotationCombine;
    }

    @Override
    public long getId() {
      return annotationCombine.getId();
    }

    @Override
    public AnnotationTypeEnum getAnnotationType() {
      return AnnotationTypeEnum.getByValue(annotationCombine.getAnnotationType());
    }

    @Override
    protected String getText() {
      return annotationCombine.getTerm();
    }
  }

  static class AnnotationFinal extends BaseAnnotationCombine {
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

  static class AnnotationReviewed extends BaseAnnotationCombine {
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

  static class AnnotationBlock extends BaseAnnotation {
    private final AnnotationTaskBlock block;

    AnnotationBlock(final AnnotationTaskBlock block) {
      this.block = block;
    }

    @Override
    protected String getText() {
      return block.getText();
    }

    @Override
    public long getId() {
      return block.getId();
    }

    @Override
    public AnnotationTypeEnum getAnnotationType() {
      return block.getAnnotationType();
    }

    @Override
    public String getAnnotation() {
      return block.getAnnotation();
    }

    @Override
    public void setAnnotation(final String annotation) {
      super.setAnnotation(annotation);

      block.setAnnotation(annotation);
    }
  }
}
