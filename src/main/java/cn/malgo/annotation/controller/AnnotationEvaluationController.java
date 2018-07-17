package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.GetDocByTaskBiz;
import cn.malgo.annotation.request.GetDocByTaskRequest;
import cn.malgo.annotation.result.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
public class AnnotationEvaluationController extends BaseController {
  private final GetDocByTaskBiz getDocByTaskBiz;

  public AnnotationEvaluationController(GetDocByTaskBiz getDocByTaskBiz) {
    this.getDocByTaskBiz = getDocByTaskBiz;
  }

  @RequestMapping(value = "/get-doc", method = RequestMethod.GET)
  public Response GetDocByTask(GetDocByTaskRequest getDocByTaskRequest) {
    return new Response<>(getDocByTaskBiz.process(getDocByTaskRequest, 0, 0));
  }
}
