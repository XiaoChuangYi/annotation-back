package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.task.AddDocsToTaskBiz;
import cn.malgo.annotation.biz.task.CreateTaskBiz;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.task.AddDocsToTaskRequest;
import cn.malgo.annotation.request.task.CreateTaskRequest;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.vo.AddDocsToTaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/task")
@Slf4j
public class TaskController {
  private final CreateTaskBiz createTaskBiz;
  private final AddDocsToTaskBiz addDocsToTaskBiz;

  public TaskController(
      final CreateTaskBiz createTaskBiz, final AddDocsToTaskBiz addDocsToTaskBiz) {
    this.createTaskBiz = createTaskBiz;
    this.addDocsToTaskBiz = addDocsToTaskBiz;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public Response<AnnotationTask> create(
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount,
      @RequestBody CreateTaskRequest request) {
    return new Response<>(createTaskBiz.process(request, userAccount));
  }

  @RequestMapping(value = "/addDocs", method = RequestMethod.POST)
  public Response<AddDocsToTaskResponse> addDocs(
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount,
      @RequestBody AddDocsToTaskRequest request) {
    return new Response<>(addDocsToTaskBiz.process(request, userAccount));
  }
}
