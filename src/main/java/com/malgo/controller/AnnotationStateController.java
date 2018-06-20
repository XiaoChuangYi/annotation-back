package com.malgo.controller;

import com.malgo.biz.brat.task.AnnotationAbandonBiz;
import com.malgo.biz.brat.task.AnnotationCommitBiz;
import com.malgo.biz.brat.task.AnnotationExamineBiz;
import com.malgo.entity.UserAccount;
import com.malgo.request.AnnotationStateRequest;
import com.malgo.request.brat.CommitAnnotationRequest;
import com.malgo.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Created by cjl on 2018/6/1. */
@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationStateController extends BaseController {

  private final AnnotationCommitBiz annotationCommitBiz;
  private final AnnotationAbandonBiz annotationAbandonBiz;
  private final AnnotationExamineBiz annotationExamineBiz;

  public AnnotationStateController(
      AnnotationCommitBiz annotationCommitBiz,
      AnnotationAbandonBiz annotationAbandonBiz,
      AnnotationExamineBiz annotationExamineBiz) {
    this.annotationCommitBiz = annotationCommitBiz;
    this.annotationAbandonBiz = annotationAbandonBiz;
    this.annotationExamineBiz = annotationExamineBiz;
  }

  /** 标注人员提交 */
  @RequestMapping(value = "commit-annotation", method = RequestMethod.POST)
  public Response commitAnnotation(
      @RequestBody CommitAnnotationRequest commitAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        annotationCommitBiz.process(
            commitAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 标注人员放弃 */
  @RequestMapping(value = "abandon-annotation", method = RequestMethod.POST)
  public Response abandonAnnotation(
      @RequestBody AnnotationStateRequest annotationStateRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        annotationAbandonBiz.process(
            annotationStateRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 审核人员审核 */
  @RequestMapping(value = "examine-annotation", method = RequestMethod.POST)
  public Response examineAnnotation(
      @RequestBody AnnotationStateRequest annotationStateRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        annotationExamineBiz.process(
            annotationStateRequest, userAccount.getId(), userAccount.getRoleId()));
  }
}
