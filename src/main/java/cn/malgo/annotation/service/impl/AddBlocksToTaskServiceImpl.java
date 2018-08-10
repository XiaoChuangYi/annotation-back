package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.AddBlocksToTaskService;
import cn.malgo.annotation.service.AnnotationSummaryService;
import cn.malgo.service.exception.BusinessRuleException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AddBlocksToTaskServiceImpl implements AddBlocksToTaskService {

  private final AnnotationSummaryService annotationSummaryService;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationRepository annotationRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public AddBlocksToTaskServiceImpl(
      final AnnotationSummaryService annotationSummaryService,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationRepository annotationRepository,
      final AnnotationTaskRepository annotationTaskRepository) {
    this.annotationSummaryService = annotationSummaryService;
    this.annotationRepository = annotationRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  public AnnotationTask addBlocksToTask(
      final AnnotationTask annotationTask, final List<Long> blockIds) {
    return addBlocksToTask(annotationTask, annotationTaskBlockRepository.findAllById(blockIds));
  }

  @Override
  public AnnotationTask addBlocksToTask(
      final AnnotationTask annotationTask, final Collection<AnnotationTaskBlock> blocks) {
    final List<AnnotationNew> annotationNews = new ArrayList<>();
    blocks.forEach(
        annotationTaskBlock -> {
          annotationTaskBlock.setState(AnnotationTaskState.DOING);
          final AnnotationNew annotationNew = new AnnotationNew();
          annotationNew.setTerm(annotationTaskBlock.getText());
          annotationNew.setAnnotationType(annotationTaskBlock.getAnnotationType());
          annotationNew.setAssignee(0);
          annotationNew.setManualAnnotation(annotationTaskBlock.getAnnotation());
          annotationNew.setFinalAnnotation(annotationTaskBlock.getAnnotation());
          annotationNew.setState(AnnotationStateEnum.UN_DISTRIBUTED);
          annotationNew.setBlockId(annotationTaskBlock.getId());
          annotationNew.setTaskId(annotationTask.getId());
          annotationNew.setDeleteToken(0l);
          annotationNew.setComment(
              String.format(
                  "[block:{%d}] add to [task:{%d}]",
                  annotationTaskBlock.getId(), annotationTask.getId()));
          annotationNews.add(annotationNew);
          annotationTask.addBlock(annotationTaskBlock);
        });
    annotationRepository.saveAll(annotationNews);
    return annotationTaskRepository.updateState(
        annotationSummaryService.updateTaskSummary(annotationTask));
  }
}
