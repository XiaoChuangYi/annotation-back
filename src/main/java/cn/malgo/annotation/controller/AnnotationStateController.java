package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.brat.task.AnnotationCommitBiz;
import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.service.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationStateController {

  private final AnnotationCommitBiz annotationCommitBiz;

  public AnnotationStateController(final AnnotationCommitBiz annotationCommitBiz) {
    this.annotationCommitBiz = annotationCommitBiz;
  }

  /** 标注人员提交 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_COMMIT)
  @RequestMapping(value = "/commit-annotation", method = RequestMethod.POST)
  public Response commitAnnotation(@RequestBody CommitAnnotationRequest commitAnnotationRequest) {
    return new Response<>(annotationCommitBiz.process(commitAnnotationRequest));
  }
}
