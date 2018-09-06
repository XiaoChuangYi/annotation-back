package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.OutsourcingPriceCalculateService;
import cn.malgo.annotation.vo.TaskInfoVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GetDoingTaskSummaryInfoBiz extends BaseBiz<Void, TaskInfoVO> {

  private final OutsourcingPriceCalculateService outsourcingPriceCalculateService;
  private final AnnotationRepository annotationRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public GetDoingTaskSummaryInfoBiz(
      final OutsourcingPriceCalculateService outsourcingPriceCalculateService,
      final AnnotationRepository annotationRepository,
      final AnnotationTaskRepository annotationTaskRepository) {
    this.outsourcingPriceCalculateService = outsourcingPriceCalculateService;
    this.annotationRepository = annotationRepository;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  protected void validateRequest(Void aVoid) throws InvalidInputException {}

  @Override
  protected TaskInfoVO doBiz(Void aVoid) {

    final List<AnnotationTask> annotationTasks =
        annotationTaskRepository.findByStateIn(
            Arrays.asList(AnnotationTaskState.DOING, AnnotationTaskState.CREATED),
            Sort.by(Direction.DESC, "createdTime"));
    if (annotationTasks.size() == 0) {
      throw new InvalidInputException("no-doing-state-task", "没有正在进行中的批次");
    }
    final AnnotationTask task = annotationTasks.get(0);
    final List<AnnotationNew> annotationNews = annotationRepository.findByTaskId(task.getId());
    final long taskStaffNum =
        annotationNews
            .parallelStream()
            .filter(annotationNew -> annotationNew.getAssignee() > 0)
            .map(annotationNew -> annotationNew.getAssignee())
            .distinct()
            .count();
    final int taskTotalWordNum =
        annotationNews.parallelStream().mapToInt(value -> value.getTerm().length()).sum();
    final int taskAnnotatedTotalWordNum =
        annotationNews
            .parallelStream()
            .filter(
                annotationNew ->
                    annotationNew.getAssignee() == 1
                        && annotationNew.getState() == AnnotationStateEnum.SUBMITTED)
            .mapToInt(value -> value.getTerm().length())
            .sum();
    final BigDecimal taskAvailableMaximumPayment =
        outsourcingPriceCalculateService
            .getUnitPriceByWordNum(taskAnnotatedTotalWordNum)
            .multiply(BigDecimal.valueOf(taskAnnotatedTotalWordNum));
    final BigDecimal predictAverageHighestPayment =
        taskStaffNum == 0
            ? BigDecimal.valueOf(0)
            : outsourcingPriceCalculateService
                .getUnitPriceByWordNum(taskTotalWordNum)
                .multiply(BigDecimal.valueOf(taskTotalWordNum))
                .divide(BigDecimal.valueOf(taskStaffNum), 2, BigDecimal.ROUND_HALF_UP);
    return new TaskInfoVO(
        task.getName(),
        taskAnnotatedTotalWordNum,
        taskAvailableMaximumPayment.equals(BigDecimal.ZERO)
            ? BigDecimal.valueOf(0)
            : taskAvailableMaximumPayment.divide(BigDecimal.valueOf(100)),
        taskStaffNum,
        predictAverageHighestPayment.equals(BigDecimal.ZERO)
            ? BigDecimal.valueOf(0)
            : predictAverageHighestPayment.divide(BigDecimal.valueOf(100)),
        annotationRepository
            .findByTaskIdAndStateIn(
                task.getId(), Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED))
            .parallelStream()
            .mapToInt(annotationNew -> annotationNew.getTerm().length())
            .sum());
  }
}
