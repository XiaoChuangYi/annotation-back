package cn.malgo.annotation.biz;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.mapper.AnnotationEvaluateInterface;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
@Slf4j
public class AnnotationEstimateQueryBiz
    extends BaseBiz<AnnotationEstimateQueryRequest, PageVO<AnnotationEstimateVO>> {

  private final AnnotationEvaluateInterface annotationEvaluateInterface;

  public AnnotationEstimateQueryBiz(AnnotationEvaluateInterface annotationEvaluateInterface) {
    this.annotationEvaluateInterface = annotationEvaluateInterface;
  }

  @Override
  protected void validateRequest(AnnotationEstimateQueryRequest annotationEstimateQueryRequest)
      throws InvalidInputException {
    if (annotationEstimateQueryRequest.getTaskId() <= 0) {
      throw new InvalidInputException("invalid-task-id", "无效的参数taskId");
    }
    if (annotationEstimateQueryRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }
    if (annotationEstimateQueryRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
  }

  @Override
  @Cacheable(cacheNames = "summary")
  public PageVO<AnnotationEstimateVO> doBiz(
      int userId, int role, AnnotationEstimateQueryRequest annotationEstimateQueryRequest) {
    final Page<AnnotationEstimateVO> page =
        PageHelper.startPage(
            annotationEstimateQueryRequest.getPageIndex(),
            annotationEstimateQueryRequest.getPageSize());
    annotationEvaluateInterface.listAnnotationEstimateSummary(
        annotationEstimateQueryRequest.getTaskId(),
        annotationEstimateQueryRequest.getWorkDay(),
        annotationEstimateQueryRequest.getAssignee());
    final PageVO<AnnotationEstimateVO> pageVO = new PageVO(page);
    log.info("测试缓存：" + System.currentTimeMillis());
    return pageVO;
  }
}
