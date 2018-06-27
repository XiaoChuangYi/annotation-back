package cn.malgo.annotation.dto;

import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@ToString(exclude = "annotation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationErrorContext implements AnnotationWithPosition {
  private static final Pattern SENTENCE_SPLITS = Pattern.compile("([,!。，！？?])");

  private int id;
  private int start;
  private int end;
  private JSONObject annotation;

  public AnnotationErrorContext(final Annotation annotation, final BratPosition position) {
    this.id = annotation.getId();
    this.start = position.getStart();
    this.end = position.getEnd();

    final AnnotationDocument document = annotation.getDocument();
    int start = this.start;
    int end = this.end;

    final String[] before = SENTENCE_SPLITS.split(document.getText().substring(0, this.start));
    if (before.length != 0) {
      start = this.start - Math.min(before[before.length - 1].length(), 5);
    }

    final String[] after = SENTENCE_SPLITS.split(document.getText().substring(this.end));
    if (after.length != 0) {
      end = this.end + Math.min(after[0].length(), 5);
    }

    this.annotation =
        getBratResult(document, Math.max(0, start), Math.min(end, document.getText().length() - 1));
  }

  @Override
  public int getAnnotationId() {
    return this.id;
  }

  private static final JSONObject getBratResult(
      final AnnotationDocument document, final int start, final int end) {
    final AnnotationDocument result =
        new AnnotationDocument(document.getText().substring(start, end + 1));
    result.setRelationEntities(Collections.emptyList());
    result.setEntities(
        document
            .getEntities()
            .stream()
            .filter(entity -> entity.getStart() >= start && entity.getEnd() <= end + 1)
            .map(
                entity ->
                    new Entity(
                        entity.getTag(),
                        entity.getStart() - start,
                        entity.getEnd() - start,
                        entity.getType(),
                        entity.getTerm()))
            .collect(Collectors.toList()));
    return AnnotationDocumentManipulator.toBratAjaxFormat(result);
  }
}
