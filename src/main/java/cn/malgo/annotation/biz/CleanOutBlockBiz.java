package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.AnnotationSummaryService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
@Slf4j
public class CleanOutBlockBiz extends TransactionalBiz<Void, Object> {
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationRepository annotationRepository;
  private final AnnotationSummaryService annotationSummaryService;
  private final AnnotationTaskRepository taskRepository;

  public CleanOutBlockBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationRepository annotationRepository,
      final AnnotationSummaryService annotationSummaryService,
      final AnnotationTaskRepository taskRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationRepository = annotationRepository;
    this.annotationSummaryService = annotationSummaryService;
    this.taskRepository = taskRepository;
  }

  @Override
  protected void validateRequest(Void req) throws InvalidInputException {}

  @Override
  protected Object doBiz(Void req, UserDetails user) {
    final List<AnnotationNew> annotations = annotationRepository.findAllPreClean();
    log.info("start cleaning out all pre clean annotations: {}", annotations.size());
    annotationRepository.saveAll(
        annotations
            .stream()
            .peek(annotationSummaryService::updateAnnotationPrecisionAndRecallRate)
            .collect(Collectors.toList()));
    log.info("call annotation news saved", annotations.size());

    final List<AnnotationTaskBlock> blocks =
        annotationTaskBlockRepository.findAllByStateIn(
            Collections.singletonList(AnnotationTaskState.PRE_CLEAN));
    log.info("start changing block states", blocks.size());
    annotationTaskBlockRepository.saveAll(
        blocks
            .stream()
            .peek(block -> block.setState(AnnotationTaskState.FINISHED))
            .collect(Collectors.toList()));
    log.info("block states all changed");

    taskRepository
        .findByStateIn(Collections.singletonList(AnnotationTaskState.FINISHED))
        .forEach(
            task -> {
              annotationSummaryService.updateTaskPersonalSummary(task);
              annotationSummaryService.updateTaskSummary(task.getId());
            });
    log.info("all task summary updated");

    // annotationTaskBlockRepository.copyDataToRelease();
    return null;
  }
}
