package cn.malgo.annotation.biz;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.mapper.AnnotationEvaluateInterface;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class AnnotationEstimateQueryBiz
    extends BaseBiz<AnnotationEstimateQueryRequest, List<AnnotationEstimateVO>> {

  private final AnnotationEvaluateInterface annotationEvaluateInterface;

  public AnnotationEstimateQueryBiz(AnnotationEvaluateInterface annotationEvaluateInterface) {
    this.annotationEvaluateInterface = annotationEvaluateInterface;
  }

  @Override
  protected void validateRequest(AnnotationEstimateQueryRequest annotationEstimateQueryRequest)
      throws InvalidInputException {}

  @Override
  protected List<AnnotationEstimateVO> doBiz(
      int userId, int role, AnnotationEstimateQueryRequest annotationEstimateQueryRequest) {
    final List<AnnotationEstimateVO> annotationEstimateVOS =
        annotationEvaluateInterface.listAnnotationEstimateSummary(
            annotationEstimateQueryRequest.getTaskId(),
            annotationEstimateQueryRequest.getWorkDay(),
            annotationEstimateQueryRequest.getAssignee());
    return annotationEstimateVOS;
  }
}
