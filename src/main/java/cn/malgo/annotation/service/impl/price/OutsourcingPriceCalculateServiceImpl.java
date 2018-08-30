package cn.malgo.annotation.service.impl.price;

import cn.malgo.annotation.constants.OutsourcePriceConsts;
import cn.malgo.annotation.constants.OutsourcePriceConsts.OutsourcePriceStage;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.PersonalAnnotatedTotalWordNumRecordRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.service.OutsourcingPriceCalculateService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class OutsourcingPriceCalculateServiceImpl implements OutsourcingPriceCalculateService {
  private final PersonalAnnotatedTotalWordNumRecordRepository
      personalAnnotatedEstimatePriceRepository;
  private final AnnotationRepository annotationRepository;

  public OutsourcingPriceCalculateServiceImpl(
      final PersonalAnnotatedTotalWordNumRecordRepository personalAnnotatedEstimatePriceRepository,
      final AnnotationRepository annotationRepository) {
    this.personalAnnotatedEstimatePriceRepository = personalAnnotatedEstimatePriceRepository;
    this.annotationRepository = annotationRepository;
  }

  @Override
  public BigDecimal getCurrentRecordEstimatedPrice(final AnnotationNew annotationNew) {
    final PersonalAnnotatedTotalWordNumRecord personalAnnotatedTotalWordNumRecord =
        personalAnnotatedEstimatePriceRepository.findByTaskIdEqualsAndAssigneeIdEquals(
            annotationNew.getTaskId(), annotationNew.getAssignee());
    if (personalAnnotatedTotalWordNumRecord == null) {
      return BigDecimal.valueOf(0);
    }
    final int totalAnnotatedWordNum =
        personalAnnotatedTotalWordNumRecord.getAnnotatedTotalWordNum();
    return getPriceByWordLength(totalAnnotatedWordNum, annotationNew.getTerm().length());
  }

  private BigDecimal getPriceByWordLength(int totalWordNum, int currentWordLength) {
    for (final OutsourcePriceStage stage : OutsourcePriceConsts.PRICE_STAGES) {
      if (totalWordNum <= stage.getWorkNum()) {
        return BigDecimal.valueOf(stage.getPrice() * currentWordLength);
      }
    }

    return BigDecimal.ZERO;
  }

  @Override
  public BigDecimal getPersonalPaymentByTaskRank(long taskId, long assigneeId) {
    final List<AnnotationNew> annotationNews =
        annotationRepository.findAllByTaskIdAndAssigneeAndStateIn(
            taskId, assigneeId, Collections.singletonList(AnnotationStateEnum.CLEANED));
    final int taskTotalEfficientWordNum =
        annotationNews
            .parallelStream()
            .mapToInt(annotationNew -> getEfficientWordNum(annotationNew, annotationNew.getTerm()))
            .sum();
    return getTaskPersonalPayment(taskTotalEfficientWordNum);
  }

  private BigDecimal getTaskPersonalPayment(int taskTotalEfficientWordNum) {
    BigDecimal result = BigDecimal.ZERO;
    int lastStageWordNum = 0;

    for (final OutsourcePriceStage stage : OutsourcePriceConsts.PRICE_STAGES) {
      result =
          result.add(
              BigDecimal.valueOf(stage.getPrice())
                  .multiply(
                      BigDecimal.valueOf(
                          Math.min(
                              taskTotalEfficientWordNum - lastStageWordNum,
                              stage.getWorkNum() - lastStageWordNum))));

      if (taskTotalEfficientWordNum <= stage.getWorkNum()) {
        break;
      }

      lastStageWordNum = stage.getWorkNum();
    }

    return result.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
  }

  private int getEfficientWordNum(AnnotationNew annotationNew, String term) {
    double f1 = annotationNew.getF1();

    if (f1 >= 0.80d && f1 < 0.85d) {
      return (int) (term.length() * 0.7);
    } else if (f1 >= 0.85d && f1 < 0.90d) {
      return (int) (term.length() * 0.8);
    } else if (f1 >= 0.90d && f1 < 0.95d) {
      return (int) (term.length() * 0.9);
    } else if (f1 >= 0.95d) {
      return term.length();
    }

    return 0;
  }

  @Override
  public BigDecimal getUnitPriceByWordNum(int totalWordNum) {
    for (final OutsourcePriceStage stage : OutsourcePriceConsts.PRICE_STAGES) {
      if (totalWordNum <= stage.getWorkNum()) {
        return BigDecimal.valueOf(stage.getPrice());
      }
    }

    return BigDecimal.ZERO;
  }

  @Override
  public BigDecimal testTaskPersonalPayment(AnnotationNew annotationNew, String term) {
    return getTaskPersonalPayment(getEfficientWordNum(annotationNew, term));
  }
}
