package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.brat.AnnotationReworkBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationBatchExamineBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationCommitBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationExamineBiz;
import cn.malgo.annotation.request.AnnotationStateBatchRequest;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.request.AnnotationStateResetRequest;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationStateController extends BaseController {

  private final AnnotationCommitBiz annotationCommitBiz;
  private final AnnotationExamineBiz annotationExamineBiz;
  private final AnnotationReworkBiz annotationReworkBiz;
  private final AnnotationBatchExamineBiz annotationBatchExamineBiz;

  public AnnotationStateController(
      AnnotationCommitBiz annotationCommitBiz,
      AnnotationExamineBiz annotationExamineBiz,
      AnnotationReworkBiz annotationReworkBiz,
      final AnnotationBatchExamineBiz annotationBatchExamineBiz) {
    this.annotationCommitBiz = annotationCommitBiz;
    this.annotationExamineBiz = annotationExamineBiz;
    this.annotationReworkBiz = annotationReworkBiz;
    this.annotationBatchExamineBiz = annotationBatchExamineBiz;
  }

  /** 标注人员提交 */
  @RequestMapping(value = "/commit-annotation", method = RequestMethod.POST)
  public Response commitAnnotation(
      @RequestBody CommitAnnotationRequest commitAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(annotationCommitBiz.process(commitAnnotationRequest, userAccount));
  }

  /** 审核人员审核 */
  @RequestMapping(value = "/examine-annotation", method = RequestMethod.POST)
  public Response examineAnnotation(
      @RequestBody AnnotationStateRequest annotationStateRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(annotationExamineBiz.process(annotationStateRequest, userAccount));
  }

  /** 审核人员打回返工操作 */
  @RequestMapping(value = "/annotation-rework", method = RequestMethod.POST)
  public Response annotationRework(
      @RequestBody AnnotationStateResetRequest annotationStateResetRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(annotationReworkBiz.process(annotationStateResetRequest, userAccount));
  }

  /** 批量审核 */
  @RequestMapping(value = "/batch-examine-annotation", method = RequestMethod.POST)
  public Response batchExamineAnnotation(
      @RequestBody AnnotationStateBatchRequest annotationStateBatchRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(
        annotationBatchExamineBiz.process(annotationStateBatchRequest, userAccount));
  }
}
