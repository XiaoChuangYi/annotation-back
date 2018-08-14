package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTask;
import java.math.BigDecimal;

public interface OutsourcingPriceCalculateService {
  BigDecimal getCurrentRecordEstimatedPrice(AnnotationNew annotationNew);

  BigDecimal getPersonalPaymentByTaskRank(long taskId, long assigneeId);

  BigDecimal getUnitPriceByWordNum(int totalWordNum);

  BigDecimal testTaskPersonalPayment(double precisionRate, String term);
}
