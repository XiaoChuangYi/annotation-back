package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.PersonalAnnotatedEstimatePriceRepository;
import cn.malgo.annotation.request.PersonalTaskSummaryRecordRequest;
import cn.malgo.annotation.service.OutsourcingPriceCalculateService;
import cn.malgo.annotation.vo.PersonalTaskRankSummaryVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PersonalTaskSummaryRecordBiz
    extends BaseBiz<PersonalTaskSummaryRecordRequest, List<PersonalTaskRankSummaryVO>> {

  private final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository;
  private final OutsourcingPriceCalculateService outsourcingPriceCalculateService;

  public PersonalTaskSummaryRecordBiz(
      final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository,
      final OutsourcingPriceCalculateService outsourcingPriceCalculateService) {
    this.personalAnnotatedEstimatePriceRepository = personalAnnotatedEstimatePriceRepository;
    this.outsourcingPriceCalculateService = outsourcingPriceCalculateService;
  }

  @Override
  protected void validateRequest(PersonalTaskSummaryRecordRequest request)
      throws InvalidInputException {
    if (request.getAssigneeId() <= 0) {
      throw new InvalidInputException("invalid-assignee-id", "无效的参数assigneeId");
    }
  }

  @Override
  protected List<PersonalTaskRankSummaryVO> doBiz(
      PersonalTaskSummaryRecordRequest request, UserDetails user) {
    return personalAnnotatedEstimatePriceRepository
        .findAllByAssigneeIdIn(request.getAssigneeId())
        .parallelStream()
        .map(
            current -> {
              PersonalTaskRankSummaryVO personalTaskRankSummaryVO = new PersonalTaskRankSummaryVO();
              BeanUtils.copyProperties(current, personalTaskRankSummaryVO);
              personalTaskRankSummaryVO.setPayment(
                  outsourcingPriceCalculateService.getPersonalPaymentByTaskRank(
                      current.getTaskId(), current.getAssigneeId()));
              return personalTaskRankSummaryVO;
            })
        .collect(Collectors.toList());
  }
}
