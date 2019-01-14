// package cn.malgo.annotation.service.impl;
//
// import cn.malgo.annotation.dto.Annotation;
// import cn.malgo.annotation.dto.error.FixAnnotationEntity;
// import cn.malgo.annotation.enums.AnnotationTypeEnum;
// import cn.malgo.annotation.service.impl.error.NewWordErrorProvider;
// import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
// import cn.malgo.annotation.utils.entity.AnnotationDocument;
// import cn.malgo.service.exception.InvalidInputException;
// import org.testng.Assert;
// import org.testng.annotations.DataProvider;
// import org.testng.annotations.Test;
//
// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
//
// public class NewWordErrorProviderTest {
//  private NewWordErrorProvider fixAnnotationErrorService = new NewWordErrorProvider(null, 200);
//
//  @DataProvider(name = "fix-annotation-data")
//  public Object[][] getData() {
//    return new Object[][] {
//      new Object[] {
//        "01234等大等圆01234",
//        "T1\twrong 5 9\t等大等圆",
//        5,
//        9,
//        Arrays.asList(
//            new FixAnnotationEntity("correct1", "等大"), new FixAnnotationEntity("correct2", "等圆")),
//        "T1\tcorrect1 5 7\t等大\nT2\tcorrect2 7 9\t等圆"
//      },
//      new Object[] {
//        "等大等圆01234",
//        "T1\twrong 0 4\t等大等圆",
//        0,
//        4,
//        Arrays.asList(
//            new FixAnnotationEntity("correct1", "等大"), new FixAnnotationEntity("correct2", "等圆")),
//        "T1\tcorrect1 0 2\t等大\nT2\tcorrect2 2 4\t等圆"
//      },
//      new Object[] {
//        "01234摩擦音01234",
//        "T1\twrong 6 8\t擦音",
//        6,
//        8,
//        Collections.singletonList(new FixAnnotationEntity("correct1", "摩擦音")),
//        "T1\tcorrect1 5 8\t摩擦音"
//      },
//      new Object[] {
//        "01234摩擦音01234摩擦音",
//        "T1\twrong 6 8\t擦音",
//        6,
//        8,
//        Collections.singletonList(new FixAnnotationEntity("correct1", "摩擦音")),
//        "T1\tcorrect1 5 8\t摩擦音"
//      },
//      new Object[] {
//        "摩擦音34567摩擦音01234",
//        "T1\twrong 9 11\t擦音",
//        9,
//        11,
//        Collections.singletonList(new FixAnnotationEntity("correct1", "摩擦音")),
//        "T1\tcorrect1 8 11\t摩擦音"
//      },
//      new Object[] {
//        "01234摩擦音01234摩擦音",
//        "T1\twrong 6 8\t擦音",
//        6,
//        8,
//        Arrays.asList(
//            new FixAnnotationEntity("correct1", "4摩擦"), new FixAnnotationEntity("correct2",
// "音01")),
//        "T1\tcorrect1 4 7\t4摩擦\nT2\tcorrect2 7 10\t音01"
//      },
//    };
//  }
//
//  @Test(dataProvider = "fix-annotation-data")
//  public void testFixAnnotation(
//      final String text,
//      final String annotationText,
//      final Integer start,
//      final Integer end,
//      final List<FixAnnotationEntity> entities,
//      final String result) {
//    final Annotation annotation = new TestAnnotation(text, annotationText);
//    fixAnnotationErrorService.fix(annotation, start, end, entities);
//    Assert.assertEquals(annotation.getAnnotation(), result);
//  }
//
//  @Test(
//      expectedExceptions = InvalidInputException.class,
//      expectedExceptionsMessageRegExp = "未在\"01234等大等圆01234\"中找到开始小于5，结束大于等于9的字符串\"等大等圆a\"")
//  public void testFixAnnotationNotFoundException() {
//    final Annotation annotation = new TestAnnotation("01234等大等圆01234", "T1\twrong 5 9\t等大等圆");
//
//    fixAnnotationErrorService.fix(
//        annotation,
//        5,
//        9,
//        Arrays.asList(
//            new FixAnnotationEntity("correct1", "等大"), new FixAnnotationEntity("correct2",
// "等圆a")));
//  }
//
//  @Test(
//      expectedExceptions = InvalidInputException.class,
//      expectedExceptionsMessageRegExp = "想要修复的部分和已经标注的集合不完全匹配")
//  public void testFixAnnotationNotContainsException() {
//    final Annotation annotation =
//        new TestAnnotation("01234等大等圆01234", "T1\tcorrect1 5 7\t等大\nT2\tcorrect2 7 9\t等圆");
//
//    fixAnnotationErrorService.fix(
//        annotation,
//        5,
//        7,
//        Arrays.asList(
//            new FixAnnotationEntity("correct1", "等大"), new FixAnnotationEntity("correct2", "等")));
//  }
//
//  @Test(
//      expectedExceptions = InvalidInputException.class,
//      expectedExceptionsMessageRegExp = "\"等大等\"必须包含\"等大等圆\"")
//  public void testFixAnnotationNotMatchManualAnnotation() {
//    final Annotation annotation = new TestAnnotation("01234等大等圆01234", "T1\twrong 5 9\t等大等圆");
//    fixAnnotationErrorService.fix(
//        annotation,
//        5,
//        9,
//        Arrays.asList(
//            new FixAnnotationEntity("correct1", "等大"), new FixAnnotationEntity("correct2", "等")));
//  }
//
//  public static class TestAnnotation implements Annotation {
//    private final String text;
//    private String annotationText;
//    private AnnotationDocument document;
//
//    public TestAnnotation(final String text, final String annotationText) {
//      this.text = text;
//      this.annotationText = annotationText;
//    }
//
//    @Override
//    public long getId() {
//      return 0;
//    }
//
//    @Override
//    public AnnotationTypeEnum getAnnotationType() {
//      return AnnotationTypeEnum.wordPos;
//    }
//
//    @Override
//    public String getAnnotation() {
//      return annotationText;
//    }
//
//    @Override
//    public void setAnnotation(String annotation) {
//      annotationText = annotation;
//      document = null;
//    }
//
//    @Override
//    public AnnotationDocument getDocument() {
//      if (document == null) {
//        document = new AnnotationDocument(this.text);
//        AnnotationDocumentManipulator.parseBratAnnotation(getAnnotation(), document);
//      }
//
//      return document;
//    }
//  }
// }
