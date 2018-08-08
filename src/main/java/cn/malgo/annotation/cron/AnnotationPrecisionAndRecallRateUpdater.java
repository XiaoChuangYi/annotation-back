package cn.malgo.annotation.cron;

import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.AnnotationSummaryService;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class AnnotationPrecisionAndRecallRateUpdater {
  private static final String CRON_STR = "0 0/20 * * * ?";

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationSummaryService annotationSummaryService;

  public AnnotationPrecisionAndRecallRateUpdater(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationSummaryService annotationSummaryService) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationSummaryService = annotationSummaryService;
  }

  @Scheduled(cron = CRON_STR)
  @Transactional
  public void updatePrecisionAndRecallRateOnce() {
    log.info("updatePrecisionAndRecallRateOnce, start: {}", new Date());
    getTasks()
        .parallelStream()
        .forEach(task -> annotationSummaryService.updateAnnotationPrecisionAndRecallRate(task));
    log.info("updatePrecisionAndRecallRateOnce, end: {}", new Date());
  }

  private List<AnnotationTask> getTasks() {
    return annotationTaskRepository.findByStateIn(
        Collections.singletonList(AnnotationTaskState.FINISHED));
  }
}
