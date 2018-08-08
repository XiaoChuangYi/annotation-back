package cn.malgo.annotation.service.impl.price;

import cn.malgo.annotation.dao.PersonalAnnotatedEstimatePriceRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import cn.malgo.annotation.service.OutsourcingPriceCalculateService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class OutsourcingPriceCalculateServiceImpl implements OutsourcingPriceCalculateService {

  private final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository;

  public OutsourcingPriceCalculateServiceImpl(
      final PersonalAnnotatedEstimatePriceRepository personalAnnotatedEstimatePriceRepository) {
    this.personalAnnotatedEstimatePriceRepository = personalAnnotatedEstimatePriceRepository;
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
    switch (getPriceRankByWordNum(totalWordNum)) {
      case 0:
        return BigDecimal.valueOf(0);
      case 1:
        return BigDecimal.valueOf(2 * currentWordLength);
      case 2:
        return BigDecimal.valueOf(3 * currentWordLength);
      case 3:
        return BigDecimal.valueOf(4 * currentWordLength);
      case 4:
        return BigDecimal.valueOf(6 * currentWordLength);
      default:
        return BigDecimal.valueOf(0);
    }
  }

  @Override
  public BigDecimal getPersonalPaymentByTaskRank(long taskId, long assigneeId) {
    final PersonalAnnotatedTotalWordNumRecord current =
        personalAnnotatedEstimatePriceRepository.findByTaskIdEqualsAndAssigneeIdEquals(
            taskId, assigneeId);
    if (current == null) {
      return BigDecimal.valueOf(0);
    }
    switch (getPriceRankByPrecisionRate(current.getPrecisionRate())) {
      case 0:
        return getPriceByWordLength(
                current.getAnnotatedTotalWordNum(), current.getAnnotatedTotalWordNum())
            .multiply(BigDecimal.valueOf(0));
      case 1:
        return getPriceByWordLength(
                current.getAnnotatedTotalWordNum(), current.getAnnotatedTotalWordNum())
            .multiply(BigDecimal.valueOf(0.7));
      case 2:
        return getPriceByWordLength(
                current.getAnnotatedTotalWordNum(), current.getAnnotatedTotalWordNum())
            .multiply(BigDecimal.valueOf(0.8));
      case 3:
        return getPriceByWordLength(
                current.getAnnotatedTotalWordNum(), current.getAnnotatedTotalWordNum())
            .multiply(BigDecimal.valueOf(0.9));
      case 4:
        return getPriceByWordLength(
            current.getAnnotatedTotalWordNum(), current.getAnnotatedTotalWordNum());
    }
    return BigDecimal.valueOf(0);
  }

  private int getPriceRankByPrecisionRate(double currentPrecisionRate) {
    if (currentPrecisionRate < 0.80) {
      return 0;
    }
    if (currentPrecisionRate >= 0.80 && currentPrecisionRate < 0.85) {
      return 1;
    }
    if (currentPrecisionRate >= 0.85 && currentPrecisionRate < 0.90) {
      return 2;
    }
    if (currentPrecisionRate >= 0.90 && currentPrecisionRate < 0.95) {
      return 3;
    }
    if (currentPrecisionRate >= 0.95) {
      return 4;
    }
    return 0;
  }

  private int getPriceRankByWordNum(int totalAnnotationWordNum) {
    if (totalAnnotationWordNum >= 0 && totalAnnotationWordNum < 20000) {
      return 1;
    }
    if (totalAnnotationWordNum >= 20000 && totalAnnotationWordNum < 30000) {
      return 2;
    }
    if (totalAnnotationWordNum >= 30000 && totalAnnotationWordNum < 40000) {
      return 3;
    }
    if (totalAnnotationWordNum >= 40000) {
      return 4;
    }
    return 0;
  }
}
