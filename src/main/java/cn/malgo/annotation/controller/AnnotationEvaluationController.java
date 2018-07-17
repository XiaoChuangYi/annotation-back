package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.AnnotationEstimateQueryBiz;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationEvaluationController {

  private final AnnotationEstimateQueryBiz annotationEstimateQueryBiz;

  public AnnotationEvaluationController(AnnotationEstimateQueryBiz annotationEstimateQueryBiz) {
    this.annotationEstimateQueryBiz = annotationEstimateQueryBiz;
  }

  @RequestMapping(value = "/query-annotation-estimate", method = RequestMethod.GET)
  public Response<List<AnnotationEstimateVO>> queryAnnotationEstimate(
      AnnotationEstimateQueryRequest annotationEstimateQueryRequest) {
    return new Response<>(
        annotationEstimateQueryBiz.process(
            annotationEstimateQueryRequest, 0, AnnotationRoleStateEnum.admin.getRole()));
  }
}
