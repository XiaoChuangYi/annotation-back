package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.AnnotationEstimateQueryBiz;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import cn.malgo.annotation.vo.AnnotationStaffEvaluateVO;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
public class AnnotationEvaluationController extends BaseController {
  private final AnnotationEstimateQueryBiz annotationEstimateQueryBiz;

  public AnnotationEvaluationController(
      final AnnotationEstimateQueryBiz annotationEstimateQueryBiz) {
    this.annotationEstimateQueryBiz = annotationEstimateQueryBiz;
  }

  @RequestMapping(value = "/query-annotation-estimate", method = RequestMethod.GET)
  public Response<AnnotationStaffEvaluateVO> queryAnnotationEstimate(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      AnnotationEstimateQueryRequest annotationEstimateQueryRequest) {
    return new Response<>(
        annotationEstimateQueryBiz.process(annotationEstimateQueryRequest, userAccount));
  }
}
