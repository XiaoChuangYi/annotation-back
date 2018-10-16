package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.brat.task.AnnotationBatchCommitBiz;
import cn.malgo.annotation.biz.brat.task.AnnotationCommitBiz;
import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.request.anno.BatchCommitAnnotationRequest;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.common.auth.user.UserDetailService;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationStateController {

  private final AnnotationCommitBiz annotationCommitBiz;
  private final UserDetailService userDetailService;
  private final AnnotationBatchCommitBiz annotationBatchCommitBiz;

  public AnnotationStateController(
      final AnnotationCommitBiz annotationCommitBiz,
      final UserDetailService userDetailService,
      final AnnotationBatchCommitBiz annotationBatchCommitBiz) {
    this.annotationCommitBiz = annotationCommitBiz;
    this.userDetailService = userDetailService;
    this.annotationBatchCommitBiz = annotationBatchCommitBiz;
  }

  /** 标注人员提交 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_COMMIT)
  @RequestMapping(value = "/commit-annotation", method = RequestMethod.POST)
  public Response commitAnnotation(
      @RequestBody CommitAnnotationRequest commitAnnotationRequest,
      final HttpServletRequest request) {
    final UserDetails userDetails = userDetailService.getUserDetails(request);
    return new Response<>(annotationCommitBiz.process(commitAnnotationRequest, userDetails));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_BATCH_COMMIT)
  @RequestMapping(value = "/batch-commit-annotation", method = RequestMethod.POST)
  public Response batchCommitAnnotation(
      @RequestBody BatchCommitAnnotationRequest request, final HttpServletRequest servletRequest) {
    final UserDetails userDetails = userDetailService.getUserDetails(servletRequest);
    return new Response<>(annotationBatchCommitBiz.process(request, userDetails));
  }
}
