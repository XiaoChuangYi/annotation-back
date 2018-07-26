package cn.malgo.annotation.cron;

import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.AnnotationSummaryService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class AnnotationSummaryUpdater {
  /** 0 0/5 * * * ? 秒 分 时 天(月) 月 天(星期) 天(月),天(星期)互斥，任意一个为? 当前的corn表达式的意思时每隔5分钟触发一次 */
  private static final String CRON_STR = "0 0/20 * * * ?";

  private final AnnotationTaskRepository taskRepository;
  private final AnnotationSummaryService annotationSummaryService;

  public AnnotationSummaryUpdater(
      final AnnotationTaskRepository taskRepository,
      final AnnotationSummaryService annotationSummaryService) {
    this.taskRepository = taskRepository;
    this.annotationSummaryService = annotationSummaryService;
  }

  @Scheduled(cron = CRON_STR)
  @Transactional
  public void updateAnnotationSummaries() {
    log.info("updateAnnotationSummaries, start: {}", new Date());

    try {
      final List<AnnotationTask> annotationTasks =
          getTasks()
              .stream()
              .map(annotationSummaryService::updateTaskSummary)
              .collect(Collectors.toList());
      taskRepository.saveAll(annotationTasks);
    } catch (Exception ex) {
      log.info("定时任务，更新task表，发生异常:{}", ex.getMessage());
    }

    log.info("updateAnnotationSummaries, end: {}", new Date());
  }

  private List<AnnotationTask> getTasks() {
    return taskRepository.findByStateNotIn(
        Arrays.asList(AnnotationTaskState.CREATED, AnnotationTaskState.FINISHED));
  }
}
