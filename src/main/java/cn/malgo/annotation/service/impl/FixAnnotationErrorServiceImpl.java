package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.FixAnnotationEntity;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.service.FixAnnotationErrorService;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FixAnnotationErrorServiceImpl implements FixAnnotationErrorService {
  @Override
  public List<Entity> fixAnnotationError(
      final AnnotationErrorEnum errorType,
      final Annotation annotation,
      final int start,
      final int end,
      final List<FixAnnotationEntity> entities) {
    switch (errorType) {
      case NEW_WORD:
        return fixNewWord(annotation, start, end, entities);

      case ENTITY_MULTIPLE_TYPE:
        if (entities.size() != 1) {
          throw new IllegalArgumentException("ENTITY_MULTIPLE_TYPE 修复只接受一个修复对象，实际得到: " + entities);
        }

        return fixEntityMultipleType(annotation, start, end, entities.get(0));
    }

    throw new IllegalArgumentException("invalid-error-type: " + errorType);
  }

  private List<Entity> fixNewWord(
      final Annotation annotation,
      final int start,
      final int end,
      final List<FixAnnotationEntity> fixEntities) {
    final String targetText =
        String.join(
            "",
            fixEntities.stream().map(FixAnnotationEntity::getTerm).collect(Collectors.toList()));

    final String text = annotation.getDocument().getText();

    if (!targetText.contains(text.substring(start, end))) {
      // 修复的字符串合并以后必须至少包含原字符串
      // 例如将  摩擦伤 -> 摩擦   是不被允许的
      throw new InvalidInputException(
          "invalid-fix-annotation",
          String.format("\"%s\"必须包含\"%s\"", targetText, text.substring(start, end)));
    }

    // 从end - targetText.length()开始寻找
    int index = Math.max(0, end - targetText.length());
    while ((index = text.indexOf(targetText, index)) != -1) {
      if (index >= end) {
        break;
      }

      if (index <= start && index + targetText.length() >= end) {
        // 找到了词条
        final int realStart = index;
        final int realEnd = index + targetText.length();

        annotation.getDocument().getEntities().sort(Comparator.comparingInt(Entity::getStart));
        if (annotation
            .getDocument()
            .getEntities()
            .stream()
            .anyMatch(
                entity ->
                    (entity.getStart() < realStart && entity.getEnd() > realStart)
                        || (entity.getStart() < realEnd && entity.getEnd() > realEnd))) {
          // 如果存在已经标注的部分和想要修复的部分有交集但不完全包含，则失败
          // 例如，"abcdef"这句话，修复为"cde"，但是"bcd"被标记过，则失败
          throw new InvalidInputException("invalid-fix-annotation", "想要修复的部分和已经标注的集合不完全匹配");
        }

        final List<Entity> entities =
            annotation
                .getDocument()
                .getEntities()
                .stream()
                .filter(entity -> entity.getEnd() <= realStart || entity.getStart() >= realEnd)
                .collect(Collectors.toList());

        for (int i = 0; i < entities.size(); ++i) {
          entities.get(i).setTag("T" + (i + 1));
        }

        final int newEntities = entities.size();
        int now = realStart;
        for (FixAnnotationEntity entity : fixEntities) {
          entities.add(
              new Entity(
                  "T" + (entities.size() + 1),
                  now,
                  now + entity.getTerm().length(),
                  entity.getType(),
                  entity.getTerm()));
          now += entity.getTerm().length();
        }

        annotation.setAnnotation(
            AnnotationDocumentManipulator.toBratAnnotations(
                new AnnotationDocument(
                    annotation.getDocument().getText(), new ArrayList<>(), entities)));
        return entities.subList(newEntities, entities.size());
      }

      index += targetText.length();
    }

    throw new InvalidInputException(
        "invalid-fix-annotation",
        String.format("未在\"%s\"中找到开始小于%s，结束大于等于%s的字符串\"%s\"", text, start, end, targetText));
  }

  private List<Entity> fixEntityMultipleType(
      final Annotation annotation,
      final int start,
      final int end,
      final FixAnnotationEntity fixEntity) {
    final AnnotationDocument oldDoc = annotation.getDocument();
    final AnnotationDocument newDoc =
        new AnnotationDocument(
            oldDoc.getText(),
            oldDoc.getRelationEntities(),
            oldDoc
                .getEntities()
                .stream()
                .map(
                    entity -> {
                      if (entity.getStart() != start || entity.getEnd() != end) {
                        return entity;
                      }

                      if (!StringUtils.equals(
                          oldDoc.getText().substring(start, end), fixEntity.getTerm())) {
                        throw new IllegalArgumentException(
                            "mismatch term: "
                                + fixEntity.getTerm()
                                + ", annotation id: "
                                + annotation.getId()
                                + ", start: "
                                + start
                                + ", end: "
                                + end);
                      }

                      return new Entity(
                          entity.getTag(), start, end, fixEntity.getType(), fixEntity.getTerm());
                    })
                .collect(Collectors.toList()));

    annotation.setAnnotation(AnnotationDocumentManipulator.toBratAnnotations(newDoc));

    return newDoc
        .getEntities()
        .stream()
        .filter(entity -> entity.getStart() == start && entity.getEnd() == end)
        .collect(Collectors.toList());
  }
}
