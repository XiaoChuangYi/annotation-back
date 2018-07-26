package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.task.AddBlocksToTaskBiz;
import cn.malgo.annotation.biz.task.CreateTaskBiz;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.request.task.AddBlocksToTaskRequest;
import cn.malgo.annotation.request.task.CreateTaskRequest;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/task")
@Slf4j
public class TaskController extends BaseController {

  private final CreateTaskBiz createTaskBiz;
  private final AddBlocksToTaskBiz addBlocksToTaskBiz;

  public TaskController(
      final CreateTaskBiz createTaskBiz, final AddBlocksToTaskBiz addBlocksToTaskBiz) {
    this.createTaskBiz = createTaskBiz;
    this.addBlocksToTaskBiz = addBlocksToTaskBiz;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public Response<AnnotationTaskVO> create(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody CreateTaskRequest request) {
    return new Response<>(createTaskBiz.process(request, userAccount));
  }

  @RequestMapping(value = "/add-blocks-to-task", method = RequestMethod.POST)
  public Response addBlocksToTask(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody AddBlocksToTaskRequest addBlocksToTaskRequest) {
    return new Response<>(addBlocksToTaskBiz.process(addBlocksToTaskRequest, userAccount));
  }
}
