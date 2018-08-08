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
public class PersonalAnnotatedWordNumUpdater {

  private static final String CRON_STR = "0 0/30 * * * ?";

  private final AnnotationTaskRepository taskRepository;
  private final AnnotationSummaryService annotationSummaryService;

  public PersonalAnnotatedWordNumUpdater(
      final AnnotationTaskRepository taskRepository,
      final AnnotationSummaryService annotationSummaryService) {
    this.taskRepository = taskRepository;
    this.annotationSummaryService = annotationSummaryService;
  }

  @Scheduled(cron = CRON_STR)
  @Transactional
  public void updatePersonalAnnotatedWordNum() {
    log.info("updatePersonalAnnotatedWordNum, start: {}", new Date());
    getTasks()
        .parallelStream()
        .forEach(task -> annotationSummaryService.updatePersonalAnnotatedWordNum(task));
    log.info("updatePersonalAnnotatedWordNum, end: {}", new Date());
  }

  private List<AnnotationTask> getTasks() {
    return taskRepository.findByStateNotIn(
        Arrays.asList(AnnotationTaskState.DOING, AnnotationTaskState.ANNOTATED));
  }
}
