package cn.malgo.annotation.service.impl;

import cn.malgo.core.definition.Entity;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.FixAnnotationEntity;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.service.FixAnnotationErrorService;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FixAnnotationErrorServiceImpl implements FixAnnotationErrorService {
  @Override
  public List<Entity> fixAnnotation(
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
}
