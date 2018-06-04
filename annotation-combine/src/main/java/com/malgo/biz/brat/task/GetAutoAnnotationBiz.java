package com.malgo.biz.brat.task;

import com.malgo.biz.BaseBiz;
import com.malgo.dto.AutoAnnotation;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.service.AlgorithmApiService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/5/31.
 */
@Component
public class GetAutoAnnotationBiz extends BaseBiz<Integer, String> {

  private final AlgorithmApiService algorithmApiService;

  @Autowired
  public GetAutoAnnotationBiz(AlgorithmApiService algorithmApiService) {
    this.algorithmApiService = algorithmApiService;
  }

  @Override
  protected void validateRequest(Integer integer) throws InvalidInputException {
    if (integer.intValue() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的标注id");
    }
  }

  @Override
  protected void authorize(int userId, int role, Integer integer) throws BusinessRuleException {

  }

  @Override
  protected String doBiz(Integer integer) {
    List<AutoAnnotation> autoAnnotationList = algorithmApiService
        .listAutoAnnotationThroughAlgorithm(integer);
    if (autoAnnotationList != null && autoAnnotationList.size() > 0) {
      AutoAnnotation autoAnnotation = autoAnnotationList.get(0);
      if (autoAnnotation != null) {
        return autoAnnotation.getAnnotation();
      } else {
        return "";
      }
    } else {
      return "";
    }
  }
}
