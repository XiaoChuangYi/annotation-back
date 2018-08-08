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
    switch (getPriceRankByWordNum(totalAnnotatedWordNum)) {
      case 0:
        return BigDecimal.valueOf(0);
      case 1:
        return BigDecimal.valueOf(2 * annotationNew.getTerm().length());
      case 2:
        return BigDecimal.valueOf(3 * annotationNew.getTerm().length());
      case 3:
        return BigDecimal.valueOf(4 * annotationNew.getTerm().length());
      case 4:
        return BigDecimal.valueOf(6 * annotationNew.getTerm().length());
    }
    return BigDecimal.valueOf(0);
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
