package cn.malgo.annotation.cron;

import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.AnnotationSummaryService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class AnnotationStaffEvaluateUpdater {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationSummaryService annotationSummaryService;

  public AnnotationStaffEvaluateUpdater(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationSummaryService annotationSummaryService) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationSummaryService = annotationSummaryService;
  }

  //  @Scheduled(cron = "${malgo.config.staff-evaluate-cron}")
  @Transactional
  public void updateAnnotationStateByExpirationTime() {
    log.info("updateAnnotationStateByExpirationTime, start: {}", new Date());
    getTasks()
        .parallelStream()
        .forEach(task -> annotationSummaryService.asyUpdateAnnotationStaffEvaluate(task));
    log.info("updateAnnotationStateByExpirationTime, end: {}", new Date());
  }

  private List<AnnotationTask> getTasks() {
    return annotationTaskRepository.findByStateIn(
        Arrays.asList(AnnotationTaskState.DOING, AnnotationTaskState.FINISHED));
  }
}
