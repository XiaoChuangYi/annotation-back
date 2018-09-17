package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.block.CleanOutBlockRequest;
import cn.malgo.annotation.service.AnnotationSummaryService;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CleanOutBlockBiz extends TransactionalBiz<CleanOutBlockRequest, Object> {

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
  protected void validateRequest(CleanOutBlockRequest request) throws InvalidInputException {}

  @Override
  protected Object doBiz(CleanOutBlockRequest request, UserDetails user) {
    final List<AnnotationNew> annotations =
        annotationRepository.findAllPreClean(
            request
                .getAnnotationTypes()
                .parallelStream()
                .map(s -> AnnotationTypeEnum.valueOf(s).ordinal())
                .collect(Collectors.toList()));
    final Map<Long, AnnotationTaskBlock> blockMap =
        annotationTaskBlockRepository
            .findAllById(
                annotations
                    .parallelStream()
                    .map(AnnotationNew::getBlockId)
                    .collect(Collectors.toSet()))
            .stream()
            .collect(Collectors.toMap(AnnotationTaskBlock::getId, block -> block));
    log.info("start cleaning out all pre clean annotations: {}", annotations.size());
    annotationRepository.saveAll(
        annotations
            .stream()
            .peek(
                ann ->
                    annotationSummaryService.updateAnnotationPrecisionAndRecallRate(ann, blockMap))
            .collect(Collectors.toList()));
    log.info("call annotation news saved", annotations.size());

    final List<AnnotationTaskBlock> blocks =
        annotationTaskBlockRepository.findAllByStateInAndAnnotationTypeIn(
            Collections.singletonList(AnnotationTaskState.PRE_CLEAN),
            request
                .getAnnotationTypes()
                .parallelStream()
                .map(AnnotationTypeEnum::valueOf)
                .collect(Collectors.toList()));
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
