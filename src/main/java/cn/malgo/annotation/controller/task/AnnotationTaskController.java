package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.task.ListAnnotationTaskBiz;
import cn.malgo.annotation.request.task.ListAnnotationTaskRequest;
import cn.malgo.annotation.result.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/task")
public class AnnotationTaskController {

  private final ListAnnotationTaskBiz listAnnotationTaskBiz;

  public AnnotationTaskController(ListAnnotationTaskBiz listAnnotationTaskBiz) {
    this.listAnnotationTaskBiz = listAnnotationTaskBiz;
  }

  /** 查询任务列表 */
  @RequestMapping(value = "/list-annotation-task", method = RequestMethod.GET)
  public Response listAnnotationTask(ListAnnotationTaskRequest listAnnotationTaskRequest) {
    return new Response(listAnnotationTaskBiz.process(listAnnotationTaskRequest, 0, 0));
  }

  /** 查询任务block列表 */
}
