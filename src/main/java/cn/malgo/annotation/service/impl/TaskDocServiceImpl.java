package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.service.TaskDocService;
import lombok.Synchronized;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaskDocServiceImpl implements TaskDocService {
  private static final int MINIMUM_WORD_BLOCK_LENGTH = 10;

  private final AnnotationTaskBlockRepository taskBlockRepository;

  public TaskDocServiceImpl(final AnnotationTaskBlockRepository taskBlockRepository) {
    this.taskBlockRepository = taskBlockRepository;
  }

  @Override
  @Synchronized
  public AnnotationTaskDoc addDocToTask(
      final AnnotationTask task, final OriginalDoc doc, final AnnotationTypeEnum annotationType) {
    final AnnotationTaskDoc taskDoc = task.addDoc(doc, annotationType);

    switch (annotationType) {
      case wordPos:
        final List<AnnotationTaskBlock> blocks = createWordPosBlocks(doc.getText());
        for (int i = 0; i < blocks.size(); ++i) {
          taskDoc.addBlock(blocks.get(i), i);
        }
        break;

      default:
        taskDoc.addBlock(getOrCreateBlock(annotationType, doc.getText()), 0);
        break;
    }

    return taskDoc;
  }

  private AnnotationTaskBlock getOrCreateBlock(
      final AnnotationTypeEnum annotationType, final String text) {
    try {
      return taskBlockRepository.findByAnnotationTypeEqualsAndTextEquals(annotationType, text);
    } catch (EntityNotFoundException ex) {
      return taskBlockRepository.save(new AnnotationTaskBlock(text, "", annotationType));
    }
  }

  private List<AnnotationTaskBlock> createWordPosBlocks(final String text) {
    final String[] sentences = text.split("，");
    final List<AnnotationTaskBlock> blocks = new ArrayList<>();
    final StringBuilder current = new StringBuilder(32);

    for (int i = 0; i < sentences.length; ++i) {
      current.append(sentences[i]).append("，");

      if (current.length() > MINIMUM_WORD_BLOCK_LENGTH || i == sentences.length - 1) {
        blocks.add(
            getOrCreateBlock(
                AnnotationTypeEnum.wordPos, current.substring(0, current.length() - 1)));
        current.setLength(0);
      }
    }

    return blocks;
  }
}
