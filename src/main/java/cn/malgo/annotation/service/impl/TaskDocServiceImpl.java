package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.TaskDocService;
import lombok.Synchronized;
import lombok.Value;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class TaskDocServiceImpl implements TaskDocService {
  private static final int MINIMUM_WORD_BLOCK_LENGTH = 10;

  private final AnnotationTaskRepository taskRepository;
  private final AnnotationTaskDocRepository taskDocRepository;
  private final AnnotationBlockService annotationBlockService;

  public TaskDocServiceImpl(
      final AnnotationTaskRepository taskRepository,
      final AnnotationTaskDocRepository taskDocRepository,
      final AnnotationBlockService annotationBlockService) {
    this.taskRepository = taskRepository;
    this.taskDocRepository = taskDocRepository;
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
        return new AddDocResult(updateState(taskDoc), result.getRight());

      default:
        final Pair<AnnotationTaskBlock, Boolean> result1 =
            annotationBlockService.getOrCreateAnnotation(annotationType, doc.getText());
        taskDoc.addBlock(result1.getLeft(), 0);
        return new AddDocResult(updateState(taskDoc), result1.getRight() ? 1 : 0);
    }
  }

  @Override
  public AnnotationTaskDoc updateState(final AnnotationTaskDoc taskDoc) {
    if (taskDoc.getBlocks().size() == 0) {
      throw new IllegalStateException("task doc " + taskDoc.getId() + " has no block");
    }

    // TaskDoc的状态是所有Block状态中的最小值
    final AnnotationTaskState state =
        taskDoc
            .getBlocks()
            .stream()
            .min(Comparator.comparing(lhs -> lhs.getBlock().getState()))
            .get()
            .getBlock()
            .getState();

    if (taskDoc.getState() != state) {
      taskDoc.setState(state);
      return taskDocRepository.save(taskDoc);
    }

    return taskDoc;
  }

  @Override
  public AnnotationTask updateState(final AnnotationTask task) {
    if (task.getTaskDocs().size() == 0) {
      return task;
    }

    final AnnotationTaskState state =
        task.getTaskDocs()
            .stream()
            .min(Comparator.comparing(AnnotationTaskDoc::getState))
            .get()
            .getState();

    if (state != task.getState()) {
      task.setState(state);
      return taskRepository.save(task);
    }

    return task;
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
