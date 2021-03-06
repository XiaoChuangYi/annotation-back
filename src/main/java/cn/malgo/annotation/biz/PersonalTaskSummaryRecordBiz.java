package cn.malgo.annotation.biz;

import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.dao.PersonalAnnotatedTotalWordNumRecordRepository;
import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import cn.malgo.annotation.request.PersonalTaskSummaryRecordRequest;
import cn.malgo.annotation.result.PageVO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersonalTaskSummaryRecordBiz
    extends BaseBiz<PersonalTaskSummaryRecordRequest, PageVO<PersonalTaskRankSummaryVO>> {

  private final PersonalAnnotatedTotalWordNumRecordRepository
      personalAnnotatedEstimatePriceRepository;
  private final OutsourcingPriceCalculateService outsourcingPriceCalculateService;

  public PersonalTaskSummaryRecordBiz(
      final PersonalAnnotatedTotalWordNumRecordRepository personalAnnotatedEstimatePriceRepository,
      final OutsourcingPriceCalculateService outsourcingPriceCalculateService) {
    this.personalAnnotatedEstimatePriceRepository = personalAnnotatedEstimatePriceRepository;
    this.outsourcingPriceCalculateService = outsourcingPriceCalculateService;
  }

  private static Specification<PersonalAnnotatedTotalWordNumRecord> queryCondition(
      PersonalTaskSummaryRecordRequest param) {
    return (Specification<PersonalAnnotatedTotalWordNumRecord>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (param.getTaskId() != 0) {
            predicates.add(criteriaBuilder.in(root.get("taskId")).value(param.getTaskId()));
          }
          if (param.getAssigneeId() != 0) {
            predicates.add(criteriaBuilder.in(root.get("assigneeId")).value(param.getAssigneeId()));
          }
          if (param.getPrecisionRate() != 0) {
            final Pair<Double, Double> pair = getPrecisionRateSection(param.getPrecisionRate());
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(root.get("precisionRate"), pair.getRight()));
            predicates.add(
                criteriaBuilder.lessThanOrEqualTo(root.get("precisionRate"), pair.getLeft()));
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

  @Override
  protected void validateRequest(PersonalTaskSummaryRecordRequest request)
      throws InvalidInputException {
    if (request.getPageIndex() < 1) {
      throw new InvalidInputException("invalid-page-index", "pageIndex??????????????????1");
    }
    if (request.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "pageSize??????????????????0");
    }
  }

  @Override
  protected PageVO<PersonalTaskRankSummaryVO> doBiz(
      PersonalTaskSummaryRecordRequest request, UserDetails user) {
    if (!user.hasPermission(PermissionConstant.ANNOTATION_TASK_DESIGNATE)) {
      request.setAssigneeId(user.getId());
    }
    final Page<PersonalAnnotatedTotalWordNumRecord> page =
        personalAnnotatedEstimatePriceRepository.findAll(
            queryCondition(request),
            PageRequest.of(request.getPageIndex() - 1, request.getPageSize()));
    final PageVO<PersonalTaskRankSummaryVO> pageVO = new PageVO(page, false);
    pageVO.setDataList(
        page.getContent()
            .parallelStream()
            .map(
                current -> {
                  PersonalTaskRankSummaryVO personalTaskRankSummaryVO =
                      new PersonalTaskRankSummaryVO();
                  BeanUtils.copyProperties(current, personalTaskRankSummaryVO);

                  if (current.getPrecisionRate() == null || current.getRecallRate() == null) {
                    personalTaskRankSummaryVO.setPrecisionRate(null);
                  } else if (current.getPrecisionRate() + current.getRecallRate() == 0) {
                    personalTaskRankSummaryVO.setPrecisionRate(0d);
                  } else {
                    personalTaskRankSummaryVO.setPrecisionRate(
                        2
                            * current.getPrecisionRate()
                            * current.getRecallRate()
                            / (current.getPrecisionRate() + current.getRecallRate()));
                  }

                  personalTaskRankSummaryVO.setPayment(
                      outsourcingPriceCalculateService.getPersonalPaymentByTaskRank(
                          current.getTaskId(), current.getAssigneeId()));
                  return personalTaskRankSummaryVO;
                })
            .collect(Collectors.toList()));
    return pageVO;
  }
}
