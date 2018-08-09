package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.PersonalAnnotatedEstimatePriceRepository;
import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import cn.malgo.annotation.request.PersonalTaskSummaryRecordRequest;
import cn.malgo.annotation.service.OutsourcingPriceCalculateService;
import cn.malgo.annotation.vo.PersonalTaskRankSummaryVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersonalTaskSummaryRecordBiz
    extends BaseBiz<PersonalTaskSummaryRecordRequest, List<PersonalTaskRankSummaryVO>> {

  private final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository;
  private final OutsourcingPriceCalculateService outsourcingPriceCalculateService;
  private final AnnotationRepository annotationRepository;

  public PersonalTaskSummaryRecordBiz(
      final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository,
      final OutsourcingPriceCalculateService outsourcingPriceCalculateService,
      final AnnotationRepository annotationRepository) {
    this.personalAnnotatedEstimatePriceRepository = personalAnnotatedEstimatePriceRepository;
    this.outsourcingPriceCalculateService = outsourcingPriceCalculateService;
    this.annotationRepository = annotationRepository;
  }

  @Override
  protected void validateRequest(PersonalTaskSummaryRecordRequest request)
      throws InvalidInputException {
    if (request.getAssigneeId() > 0 && request.getTaskId() > 0) {
      throw new InvalidInputException(
          "assignee-id-task-id-can-not-be-both", "参数assigneeId和taskId不能同时出现");
    }
  }

  @Override
  protected List<PersonalTaskRankSummaryVO> doBiz(
      PersonalTaskSummaryRecordRequest request, UserDetails user) {
    if (user.getId() != 1) {
      request.setAssigneeId(user.getId());
    }
    return personalAnnotatedEstimatePriceRepository
        .findAll(queryCondition(request))
        .parallelStream()
        .map(
            current -> {
              PersonalTaskRankSummaryVO personalTaskRankSummaryVO = new PersonalTaskRankSummaryVO();
              BeanUtils.copyProperties(current, personalTaskRankSummaryVO);
              personalTaskRankSummaryVO.setPayment(
                  outsourcingPriceCalculateService.getPersonalPaymentByTaskRank(
                      current.getTaskId(), current.getAssigneeId()));
              personalTaskRankSummaryVO.setTotalWordNum(
                  annotationRepository
                      .findAllByTaskIdEqualsAndAssigneeEquals(
                          current.getTaskId(), current.getAssigneeId())
                      .parallelStream()
                      .mapToInt(value -> value.getTerm().length())
                      .sum());
              return personalTaskRankSummaryVO;
            })
        .collect(Collectors.toList());
  }

  private static Specification<PersonalAnnotatedTotalWordNumRecord> queryCondition(
      PersonalTaskSummaryRecordRequest param) {
    return (Specification<PersonalAnnotatedTotalWordNumRecord>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (param.getTaskId() != 0) {
            predicates.add(criteriaBuilder.in(root.get("task_id")).value(param.getTaskId()));
          }
          if (param.getAssigneeId() != 0) {
            predicates.add(
                criteriaBuilder.in(root.get("assignee_id")).value(param.getAssigneeId()));
          }
          if (param.getPrecisionRate() != 0) {
            final Pair<Double, Double> pair = getPrecisionRateSection(param.getPrecisionRate());
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(root.get("precision_rate"), pair.getRight()));
            predicates.add(criteriaBuilder.lessThan(root.get("precision_rate"), pair.getLeft()));
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
  }

  private static Pair<Double, Double> getPrecisionRateSection(int flag) {
    switch (flag) {
      case 1:
        return Pair.of(1d, 0.95);
      case 2:
        return Pair.of(0.95, 0.90);
      case 3:
        return Pair.of(0.90, 0.85);
      case 4:
        return Pair.of(0.85, 0.80);
      case 5:
        return Pair.of(0.80, 0d);
      default:
        return Pair.of(0d, 0d);
    }
  }
}
