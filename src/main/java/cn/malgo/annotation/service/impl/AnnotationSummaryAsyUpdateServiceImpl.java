package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dto.AnnotationOverview;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.mapper.AnnotationEvaluateInterface;
import cn.malgo.annotation.service.AnnotationSummaryAsyncUpdateService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnotationSummaryAsyUpdateServiceImpl implements AnnotationSummaryAsyncUpdateService {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationEvaluateInterface annotationEvaluateInterface;

  public AnnotationSummaryAsyUpdateServiceImpl(
      final AnnotationTaskRepository annotationTaskRepository,
      final AnnotationEvaluateInterface annotationEvaluateInterface) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationEvaluateInterface = annotationEvaluateInterface;
  }

  /** 0 0/5 * * * ? 秒 分 时 天(月) 月 天(星期) 天(月),天(星期)互斥，任意一个为? 当前的corn表达式的意思时每隔5分钟触发一次 */
  @Override
  @Scheduled(cron = "0 0/5 * * * ?")
  public List<AnnotationTask> asyncUpdateAnnotationOverview() {
    try {
      final List<AnnotationTask> annotationTasks = annotationTaskRepository.findAll();
      log.info("now time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
      final Map<Integer, AnnotationOverview> map =
          annotationEvaluateInterface
              .listAnnotationOverviewSummary(0)
              .stream()
              .collect(
                  Collectors.toMap(
                      annotationOverview -> annotationOverview.getTaskId(),
                      annotationOverview -> annotationOverview));
      IntStream.range(0, annotationTasks.size())
          .forEach(
              i -> {
                final AnnotationTask annotationTask = annotationTasks.get(i);
                final AnnotationOverview annotationOverview = map.get(annotationTask.getId());
                if (annotationOverview != null) {
                  annotationTask.setTotalBranchNum(annotationOverview.getTotalBranch());
                  annotationTask.setAnnotatedBranchNum(annotationOverview.getAnnotatedBranch());
                  annotationTask.setAnnotatedWordNum(annotationOverview.getAnnotatedWordNum());
                  annotationTask.setRestBranchNum(annotationOverview.getRestBranch());
                  annotationTask.setRestWordNum(annotationOverview.getRestWordNum());
                  annotationTask.setTotalWordNum(annotationOverview.getTotalWordNum());
                  annotationTask.setInConformity(annotationOverview.getInConformity());
                  annotationTask.setAbandonBranchNum(annotationOverview.getAbandonBranch());
                  annotationTask.setAbandonWordNum(annotationOverview.getAbandonWordNum());
                }
              });
      return annotationTaskRepository.saveAll(annotationTasks);
    } catch (Exception ex) {
      log.info("定时任务，更新task表，发生异常:{}", ex.getMessage());
    }
    return null;
  }
}
