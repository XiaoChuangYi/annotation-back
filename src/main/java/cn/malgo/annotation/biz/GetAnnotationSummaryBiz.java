package cn.malgo.annotation.biz;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AnnotationSummary;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.vo.AnnotationSummaryVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/30. */
@Component
public class GetAnnotationSummaryBiz extends BaseBiz<Object, List<AnnotationSummaryVO>> {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public GetAnnotationSummaryBiz(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(Object o) throws InvalidInputException {}

  @Override
  protected List<AnnotationSummaryVO> doBiz(Object o) {
    List<AnnotationSummary> annotationSummaryList = annotationCombineRepository.findByStateGroup();
    List<AnnotationSummaryVO> finalAnnotationSummaryVOList =
        annotationSummaryList
            .stream()
            .map(x -> new AnnotationSummaryVO(x.getState(), x.getNum()))
            .collect(Collectors.toList());
    return finalAnnotationSummaryVOList;
  }
}
