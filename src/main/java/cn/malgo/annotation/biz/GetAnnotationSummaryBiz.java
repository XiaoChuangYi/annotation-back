package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.vo.AnnotationSummaryVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequirePermission(Permissions.ADMIN)
public class GetAnnotationSummaryBiz extends BaseBiz<Object, List<AnnotationSummaryVO>> {
  private final AnnotationRepository AnnotationRepository;

  @Autowired
  public GetAnnotationSummaryBiz(AnnotationRepository AnnotationRepository) {
    this.AnnotationRepository = AnnotationRepository;
  }

  @Override
  protected void validateRequest(Object o) throws InvalidInputException {}

  @Override
  protected List<AnnotationSummaryVO> doBiz(Object o) {
    return AnnotationRepository.findByStateGroup()
        .stream()
        .map(x -> new AnnotationSummaryVO(x.getState(), x.getNum()))
        .collect(Collectors.toList());
  }
}
