package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.task.AddDocsToTaskBiz;
import cn.malgo.annotation.biz.task.CreateTaskBiz;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.request.task.AddDocsToTaskRequest;
import cn.malgo.annotation.request.task.CreateTaskRequest;
import cn.malgo.annotation.vo.AddDocsToTaskResponse;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/task")
@Slf4j
public class TaskController extends BaseController {
  private final CreateTaskBiz createTaskBiz;
  private final AddDocsToTaskBiz addDocsToTaskBiz;

  public TaskController(
      final CreateTaskBiz createTaskBiz, final AddDocsToTaskBiz addDocsToTaskBiz) {
    this.createTaskBiz = createTaskBiz;
    this.addDocsToTaskBiz = addDocsToTaskBiz;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public Response<AnnotationTaskVO> create(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody CreateTaskRequest request) {
    return new Response<>(createTaskBiz.process(request, userAccount));
  }

  @RequestMapping(value = "/add-docs", method = RequestMethod.POST)
  public Response<AddDocsToTaskResponse> addDocs(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody AddDocsToTaskRequest request) {
    return new Response<>(addDocsToTaskBiz.process(request, userAccount));
  }
}
