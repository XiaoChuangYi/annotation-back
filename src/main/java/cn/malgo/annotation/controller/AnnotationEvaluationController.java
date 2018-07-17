package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.GetDocByTaskBiz;
import cn.malgo.annotation.request.GetDocByTaskRequest;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.biz.AnnotationEstimateQueryBiz;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
public class AnnotationEvaluationController extends BaseController {
  private final GetDocByTaskBiz getDocByTaskBiz;
  private final AnnotationEstimateQueryBiz annotationEstimateQueryBiz;

  public AnnotationEvaluationController(
      GetDocByTaskBiz getDocByTaskBiz, AnnotationEstimateQueryBiz annotationEstimateQueryBiz) {
    this.getDocByTaskBiz = getDocByTaskBiz;
    this.annotationEstimateQueryBiz = annotationEstimateQueryBiz;
  }

  @RequestMapping(value = "/get-doc", method = RequestMethod.GET)
  public Response GetDocByTask(GetDocByTaskRequest getDocByTaskRequest) {
    return new Response<>(getDocByTaskBiz.process(getDocByTaskRequest, 0, 0));
  }

  @RequestMapping(value = "/query-annotation-estimate", method = RequestMethod.GET)
  public Response<List<AnnotationEstimateVO>> queryAnnotationEstimate(
      AnnotationEstimateQueryRequest annotationEstimateQueryRequest) {
    return new Response<>(
        annotationEstimateQueryBiz.process(
            annotationEstimateQueryRequest, 0, AnnotationRoleStateEnum.admin.getRole()));
  }
}
