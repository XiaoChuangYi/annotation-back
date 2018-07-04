package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.TaskDocService;
import lombok.Synchronized;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaskDocServiceImpl implements TaskDocService {
  private static final int MINIMUM_WORD_BLOCK_LENGTH = 10;

  private final AnnotationBlockService annotationBlockService;

  public TaskDocServiceImpl(final AnnotationBlockService annotationBlockService) {
    this.annotationBlockService = annotationBlockService;
  }

  @Override
  @Synchronized
  public AddDocResult addDocToTask(
      final AnnotationTask task, final OriginalDoc doc, final AnnotationTypeEnum annotationType) {
    final AnnotationTaskDoc taskDoc = task.addDoc(doc, annotationType);

    switch (annotationType) {
      case wordPos:
        final Pair<List<AnnotationTaskBlock>, Integer> result = createWordPosBlocks(doc.getText());
        for (int i = 0; i < result.getLeft().size(); ++i) {
          taskDoc.addBlock(result.getLeft().get(i), i);
        }
        return new AddDocResult(taskDoc, result.getRight());

      default:
        final Pair<AnnotationTaskBlock, Boolean> result1 =
            annotationBlockService.getOrCreateAnnotation(annotationType, doc.getText());
        taskDoc.addBlock(result1.getLeft(), 0);
        return new AddDocResult(taskDoc, result1.getRight() ? 1 : 1);
    }
  }

  private Pair<List<AnnotationTaskBlock>, Integer> createWordPosBlocks(final String text) {
    final String[] sentences = text.split("，");
    final List<AnnotationTaskBlock> blocks = new ArrayList<>();
    final StringBuilder current = new StringBuilder(32);
    int createdCount = 0;

    for (int i = 0; i < sentences.length; ++i) {
      current.append(sentences[i]).append("，");

      if (current.length() > MINIMUM_WORD_BLOCK_LENGTH || i == sentences.length - 1) {
        final Pair<AnnotationTaskBlock, Boolean> result =
            annotationBlockService.getOrCreateAnnotation(
                AnnotationTypeEnum.wordPos, current.substring(0, current.length() - 1));
        blocks.add(result.getLeft());

        if (result.getRight()) {
          createdCount++;
        }

        current.setLength(0);
      }
    }

    return Pair.of(blocks, createdCount);
  }

  @Value
  public static final class AddDocResult {
    private final AnnotationTaskDoc taskDoc;
    private final int createdBlocks;
  }
}
