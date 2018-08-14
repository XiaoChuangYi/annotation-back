package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import org.springframework.stereotype.Service;

@Service
public class AnnotationFactoryImpl implements AnnotationFactory {

  @Override
  public Annotation create(final AnnotationTaskBlock block) {
    if (block == null) {
      throw new NullPointerException("create annotation get null");
    }

    switch (block.getState()) {
      case ANNOTATED:
      case PRE_CLEAN:
      case FINISHED:
        return new AnnotationBlock(block);
    }

    throw new IllegalArgumentException("标注状态错误: " + block.getState());
  }

  @Override
  public Annotation create(AnnotationNew annotation) {
    if (annotation == null) {
      throw new NullPointerException("create annotation get null");
    }

    switch (annotation.getState()) {
      case UN_DISTRIBUTED:
      case ANNOTATION_PROCESSING:
      case PRE_ANNOTATION:
      case SUBMITTED:
      case PRE_CLEAN:
      case CLEANED:
        return new AnnotationNewFinal(annotation);
    }

    throw new IllegalArgumentException("标注状态错误: " + annotation.getState());
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

  static class AnnotationNewFinal extends BaseAnnotation {

    private final AnnotationNew annotationNew;

    public AnnotationNewFinal(final AnnotationNew annotationNew) {
      this.annotationNew = annotationNew;
    }

    @Override
    public long getId() {
      return 0;
    }

    @Override
    public AnnotationTypeEnum getAnnotationType() {
      return null;
    }

    @Override
    public String getAnnotation() {
      return annotationNew.getFinalAnnotation();
    }

    @Override
    protected String getText() {
      return null;
    }

    @Override
    public void setAnnotation(String annotation) {
      super.setAnnotation(annotation);
      annotationNew.setFinalAnnotation(annotation);
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
