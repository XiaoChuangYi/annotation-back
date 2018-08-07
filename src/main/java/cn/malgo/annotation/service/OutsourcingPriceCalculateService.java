package cn.malgo.annotation.service;

import cn.malgo.annotation.entity.AnnotationNew;
import java.math.BigDecimal;

public interface OutsourcingPriceCalculateService {
  BigDecimal getCurrentRecordEstimatedPrice(AnnotationNew annotationNew);
}
