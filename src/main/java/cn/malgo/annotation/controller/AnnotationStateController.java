package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.brat.AnnotationReExaminationBiz;
import cn.malgo.annotation.biz.brat.AnnotationReworkBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationAbandonBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationCommitBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationExamineBiz;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.annotation.result.Response;
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
  private final AnnotationReExaminationBiz annotationReExaminationBiz;
  private final AnnotationReworkBiz annotationReworkBiz;

  public AnnotationStateController(
      AnnotationCommitBiz annotationCommitBiz,
      AnnotationAbandonBiz annotationAbandonBiz,
      AnnotationExamineBiz annotationExamineBiz,
      AnnotationReworkBiz annotationReworkBiz,
      AnnotationReExaminationBiz annotationReExaminationBiz) {
    this.annotationCommitBiz = annotationCommitBiz;
    this.annotationAbandonBiz = annotationAbandonBiz;
    this.annotationExamineBiz = annotationExamineBiz;
    this.annotationReworkBiz = annotationReworkBiz;
    this.annotationReExaminationBiz = annotationReExaminationBiz;
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
  /** */
}
