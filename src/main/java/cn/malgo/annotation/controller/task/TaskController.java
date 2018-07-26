package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.task.CreateTaskBiz;
import cn.malgo.annotation.controller.BaseController;
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

  public TaskController(final CreateTaskBiz createTaskBiz) {
    this.createTaskBiz = createTaskBiz;
  }

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public Response<AnnotationTaskVO> create(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody CreateTaskRequest request) {
    return new Response<>(createTaskBiz.process(request, userAccount));
  }
}
