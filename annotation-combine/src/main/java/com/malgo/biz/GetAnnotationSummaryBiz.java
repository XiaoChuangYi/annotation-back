package com.malgo.biz;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dto.AnnotationSummary;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.vo.AnnotationSummaryVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/5/30.
 */
@Component
public class GetAnnotationSummaryBiz extends BaseBiz<Object, List<AnnotationSummaryVO>> {

  private final AnnotationCombineRepository annotationCombineRepository;


  @Autowired
  public GetAnnotationSummaryBiz(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(Object o) throws InvalidInputException {

  }

  @Override
  protected void authorize(int userId, int role, Object o) throws BusinessRuleException {

  }

  @Override
  protected List<AnnotationSummaryVO> doBiz(Object o) {
    List<AnnotationSummary> annotationSummaryList = annotationCombineRepository.findByStateGroup();
    List<AnnotationSummaryVO> finalAnnotationSummaryVOList = annotationSummaryList.stream()
        .map(x -> new AnnotationSummaryVO(x.getState(), x.getNum())).collect(
            Collectors.toList());
    return finalAnnotationSummaryVOList;
  }
}
