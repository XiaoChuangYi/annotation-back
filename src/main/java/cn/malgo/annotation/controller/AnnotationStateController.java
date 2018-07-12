package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.brat.AnnotationReworkBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationAbandonBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationCommitBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationExamineBiz;
import cn.malgo.annotation.dto.UserDetails;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.request.AnnotationStateResetRequest;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.annotation.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/** Created by cjl on 2018/6/1. */
@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationStateController extends BaseController {

  private final AnnotationCommitBiz annotationCommitBiz;
  private final AnnotationAbandonBiz annotationAbandonBiz;
  private final AnnotationExamineBiz annotationExamineBiz;

  private final AnnotationReworkBiz annotationReworkBiz;

  public AnnotationStateController(
      AnnotationCommitBiz annotationCommitBiz,
      AnnotationAbandonBiz annotationAbandonBiz,
      AnnotationExamineBiz annotationExamineBiz,
      AnnotationReworkBiz annotationReworkBiz) {
    this.annotationCommitBiz = annotationCommitBiz;
    this.annotationAbandonBiz = annotationAbandonBiz;
    this.annotationExamineBiz = annotationExamineBiz;
    this.annotationReworkBiz = annotationReworkBiz;
  }

  /** 标注人员提交 */
  @RequestMapping(value = "/commit-annotation", method = RequestMethod.POST)
  public Response commitAnnotation(
      @RequestBody CommitAnnotationRequest commitAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(
        annotationCommitBiz.process(
            commitAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 标注人员放弃 */
  @RequestMapping(value = "/abandon-annotation", method = RequestMethod.POST)
  public Response abandonAnnotation(
      @RequestBody AnnotationStateRequest annotationStateRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(
        annotationAbandonBiz.process(
            annotationStateRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 审核人员审核 */
  @RequestMapping(value = "/examine-annotation", method = RequestMethod.POST)
  public Response examineAnnotation(
      @RequestBody AnnotationStateRequest annotationStateRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(
        annotationExamineBiz.process(
            annotationStateRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 审核人员打回返工操作 */
  @RequestMapping(value = "/annotation-rework", method = RequestMethod.POST)
  public Response annotationRework(
      @RequestBody AnnotationStateResetRequest annotationStateResetRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(
        annotationReworkBiz.process(
            annotationStateResetRequest, userAccount.getId(), userAccount.getRoleId()));
  }
}
