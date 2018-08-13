package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.PersonalAnnotatedTotalWordNumRecordRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.service.OutsourcingPriceCalculateService;
import cn.malgo.annotation.vo.TaskInfoVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GetDoingTaskSummaryInfoBiz extends BaseBiz<Void, TaskInfoVO> {

  private final PersonalAnnotatedTotalWordNumRecordRepository
      personalAnnotatedTotalWordNumRecordRepository;
  private final OutsourcingPriceCalculateService outsourcingPriceCalculateService;
  private final AnnotationRepository annotationRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public GetDoingTaskSummaryInfoBiz(
      final PersonalAnnotatedTotalWordNumRecordRepository
          personalAnnotatedTotalWordNumRecordRepository,
      final OutsourcingPriceCalculateService outsourcingPriceCalculateService,
      final AnnotationRepository annotationRepository,
      final AnnotationTaskRepository annotationTaskRepository) {
    this.personalAnnotatedTotalWordNumRecordRepository =
        personalAnnotatedTotalWordNumRecordRepository;
    this.outsourcingPriceCalculateService = outsourcingPriceCalculateService;
    this.annotationRepository = annotationRepository;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  protected void validateRequest(Void aVoid) throws InvalidInputException {}

  @Override
  protected TaskInfoVO doBiz(Void aVoid, UserDetails user) {
    final AnnotationTask task =
        annotationTaskRepository
            .findByStateIn(
                Collections.singletonList(AnnotationTaskState.DOING),
                Sort.by(Direction.DESC, "createdTime"))
            .get(0);
    if (task != null) {
      final List<PersonalAnnotatedTotalWordNumRecord> personalAnnotatedTotalWordNumRecords =
          personalAnnotatedTotalWordNumRecordRepository.findAllByTaskIdEquals(task.getId());
      final long taskStaffNum =
          personalAnnotatedTotalWordNumRecords
              .stream()
              .map(current -> current.getAssigneeId())
              .distinct()
              .count();
      final int taskTotalWordNum =
          personalAnnotatedTotalWordNumRecords
              .stream()
              .mapToInt(current -> current.getTotalWordNum())
              .sum();
      final int taskAnnotatedTotalWordNum =
          personalAnnotatedTotalWordNumRecords
              .stream()
              .filter(current -> current.getAssigneeId() == user.getId())
              .mapToInt(current -> current.getAnnotatedTotalWordNum())
              .sum();

      return new TaskInfoVO(
          task.getName(),
          taskAnnotatedTotalWordNum,
          outsourcingPriceCalculateService
              .getUnitPriceByWordNum(taskAnnotatedTotalWordNum)
              .multiply(BigDecimal.valueOf(taskAnnotatedTotalWordNum))
              .divide(BigDecimal.valueOf(100)),
          taskStaffNum,
          outsourcingPriceCalculateService
              .getUnitPriceByWordNum((int) (taskTotalWordNum / taskStaffNum))
              .multiply(BigDecimal.valueOf(taskTotalWordNum / taskStaffNum))
              .divide(BigDecimal.valueOf(100)),
          annotationRepository
              .findByTaskIdEqualsAndStateIn(
                  task.getId(), Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED))
              .parallelStream()
              .mapToInt(annotationNew -> annotationNew.getTerm().length())
              .sum());
    }
    return new TaskInfoVO();
  }
}
