package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.task.ListAnnotationTaskBiz;
import cn.malgo.annotation.biz.task.ListAnnotationTaskBlockBiz;
import cn.malgo.annotation.biz.task.ListTaskDetailsBiz;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.request.task.ListAnnotationTaskBlockRequest;
import cn.malgo.annotation.request.task.ListAnnotationTaskRequest;
import cn.malgo.annotation.request.task.ListTaskDetailRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationTaskBlockResponse;
import cn.malgo.annotation.vo.AnnotationTaskDetailVO;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2/task")
public class AnnotationTaskController extends BaseController {
  private final ListAnnotationTaskBiz listAnnotationTaskBiz;
  private final ListAnnotationTaskBlockBiz listAnnotationTaskBlockBiz;
  private final ListTaskDetailsBiz listTaskDetailsBiz;

  public AnnotationTaskController(
      final ListAnnotationTaskBiz listAnnotationTaskBiz,
      final ListAnnotationTaskBlockBiz listAnnotationTaskBlockBiz,
      final ListTaskDetailsBiz listTaskDetailsBiz) {
    this.listAnnotationTaskBiz = listAnnotationTaskBiz;
    this.listAnnotationTaskBlockBiz = listAnnotationTaskBlockBiz;
    this.listTaskDetailsBiz = listTaskDetailsBiz;
  }

  /** 查询任务列表 */
  @RequestMapping(value = "/list-annotation-tasks", method = RequestMethod.GET)
  public Response<PageVO<AnnotationTaskVO>> listAnnotationTask(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      ListAnnotationTaskRequest listAnnotationTaskRequest) {
    return new Response<>(listAnnotationTaskBiz.process(listAnnotationTaskRequest, userAccount));
  }

  /** 查询任务详情列表 */
  @RequestMapping(value = "/list-task-details/{id}", method = RequestMethod.GET)
  public Response<AnnotationTaskDetailVO> listTaskDetails(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @PathVariable("id") long id) {

    return new Response<>(listTaskDetailsBiz.process(new ListTaskDetailRequest(id), userAccount));
  }

  /** 查询任务block列表 */
  @RequestMapping(value = "/list-annotation-task-block", method = RequestMethod.GET)
  public Response<PageVO<AnnotationTaskBlockResponse>> listAnnotationTaskBlock(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      ListAnnotationTaskBlockRequest listAnnotationTaskBlockRequest) {
    return new Response<>(
        listAnnotationTaskBlockBiz.process(listAnnotationTaskBlockRequest, userAccount));
  }
}
