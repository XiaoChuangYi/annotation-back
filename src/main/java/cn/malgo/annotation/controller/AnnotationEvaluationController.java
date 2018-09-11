package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.AnnotationEstimateQueryBiz;
import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.vo.AnnotationStaffEvaluateVO;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.service.model.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
public class AnnotationEvaluationController {

  private final AnnotationEstimateQueryBiz annotationEstimateQueryBiz;

  public AnnotationEvaluationController(
      final AnnotationEstimateQueryBiz annotationEstimateQueryBiz) {
    this.annotationEstimateQueryBiz = annotationEstimateQueryBiz;
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_SUMMARY_STAFF_ESTIMATE)
  @RequestMapping(value = "/query-annotation-estimate", method = RequestMethod.GET)
  public Response<AnnotationStaffEvaluateVO> queryAnnotationEstimate(
      AnnotationEstimateQueryRequest annotationEstimateQueryRequest) {
    return new Response<>(
        annotationEstimateQueryBiz.process(annotationEstimateQueryRequest, null));
  }
}
