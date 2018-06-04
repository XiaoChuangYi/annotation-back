package com.malgo.biz;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.CountAnnotationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Created by cjl on 2018/5/31.
 */
@Component
public class CountAnnotationBiz extends BaseBiz<CountAnnotationRequest,Integer> {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public CountAnnotationBiz(AnnotationCombineRepository annotationCombineRepository){
    this.annotationCombineRepository=annotationCombineRepository;
  }
  @Override
  protected void validateRequest(CountAnnotationRequest countAnnotationRequest)
      throws InvalidInputException {
    if(countAnnotationRequest.getAnnotationTypes()==null) {
      throw new InvalidInputException("invalid-annotationTypes", "参数annotationTypes当前值无效");
    }
  }

  @Override
  protected void authorize(int userId, int role, CountAnnotationRequest countAnnotationRequest)
      throws BusinessRuleException {

  }

  @Override
  protected Integer doBiz(CountAnnotationRequest countAnnotationRequest) {
    int num;
    if(countAnnotationRequest.getAnnotationTypes().size()>0) {
       num= annotationCombineRepository
          .countAllByAnnotationTypeInAndStateEquals(countAnnotationRequest.getAnnotationTypes()
              , AnnotationCombineStateEnum.unDistributed.name());
    }else {
      num=annotationCombineRepository.countAllByStateIn(AnnotationCombineStateEnum.unDistributed.name());
    }
    return num;
  }
}
