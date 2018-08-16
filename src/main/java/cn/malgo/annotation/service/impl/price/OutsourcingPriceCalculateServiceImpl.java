package cn.malgo.annotation.service.impl.price;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.PersonalAnnotatedTotalWordNumRecordRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.PersonalAnnotatedTotalWordNumRecord;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.service.OutsourcingPriceCalculateService;
import java.math.BigDecimal;
import java.util.Arrays;
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
    final List<AnnotationNew> annotationNews =
        annotationRepository.findAllByTaskIdAndAssigneeAndStateIn(
            taskId, assigneeId, Arrays.asList(AnnotationStateEnum.CLEANED));
    final int taskTotalEfficientWordNum =
        annotationNews
            .parallelStream()
            .mapToInt(annotationNew -> getEfficientWordNum(annotationNew, annotationNew.getTerm()))
            .sum();
    return getTaskPersonalPayment(taskTotalEfficientWordNum);
  }

  private BigDecimal getTaskPersonalPayment(int taskTotalEfficientWordNum) {
    if (0 < taskTotalEfficientWordNum && taskTotalEfficientWordNum < 20000) {
      return BigDecimal.valueOf(2)
          .multiply(BigDecimal.valueOf(taskTotalEfficientWordNum))
          .divide(BigDecimal.valueOf(100));
    } else if (taskTotalEfficientWordNum >= 20000 && taskTotalEfficientWordNum < 30000) {
      return BigDecimal.valueOf(3)
          .multiply(BigDecimal.valueOf(taskTotalEfficientWordNum - 20000))
          .divide(BigDecimal.valueOf(100))
          .add(BigDecimal.valueOf(400));
    } else if (taskTotalEfficientWordNum >= 30000 && taskTotalEfficientWordNum < 40000) {
      return BigDecimal.valueOf(4)
          .multiply(BigDecimal.valueOf(taskTotalEfficientWordNum - 30000))
          .divide(BigDecimal.valueOf(100))
          .add(BigDecimal.valueOf(400))
          .add(BigDecimal.valueOf(300));
    } else if (taskTotalEfficientWordNum >= 40000) {
      return BigDecimal.valueOf(6)
          .multiply(BigDecimal.valueOf(taskTotalEfficientWordNum - 40000))
          .divide(BigDecimal.valueOf(100))
          .add(BigDecimal.valueOf(400))
          .add(BigDecimal.valueOf(300))
          .add(BigDecimal.valueOf(400));
    } else {
      return BigDecimal.valueOf(0);
    }
  }

  /** f1计算公式：f1 = 2 * p * r / (p + r) if correct_preds > 0 else 0 */
  private int getEfficientWordNum(AnnotationNew annotationNew, String term) {
    int efficientWordNum = 0;
    double f1;
    if ((annotationNew.getPrecisionRate() + annotationNew.getRecallRate()) == 0) {
      f1 = 0d;
    } else {
      f1 =
          2
              * annotationNew.getPrecisionRate()
              * annotationNew.getRecallRate()
              / (annotationNew.getPrecisionRate() + annotationNew.getRecallRate());
    }
    if (f1 >= 0.80d && f1 < 0.85d) {
      efficientWordNum += term.length() * 0.7;
    }
    if (f1 >= 0.85d && f1 < 0.90d) {
      efficientWordNum += term.length() * 0.8;
    }
    if (f1 >= 0.90d && f1 < 0.95d) {
      efficientWordNum += term.length() * 0.9;
    }
    if (f1 >= 0.95d) {
      efficientWordNum += term.length();
    }
    return efficientWordNum;
  }

  @Override
  public BigDecimal getUnitPriceByWordNum(int totalWordNum) {
    switch (getPriceRankByWordNum(totalWordNum)) {
      case 0:
        return BigDecimal.valueOf(0);
      case 1:
        return BigDecimal.valueOf(2);
      case 2:
        return BigDecimal.valueOf(3);
      case 3:
        return BigDecimal.valueOf(4);
      case 4:
        return BigDecimal.valueOf(6);
      default:
        return BigDecimal.valueOf(0);
    }
  }

  @Override
  public BigDecimal testTaskPersonalPayment(AnnotationNew annotationNew, String term) {
    return getTaskPersonalPayment(getEfficientWordNum(annotationNew, term));
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
