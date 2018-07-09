package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.task.ListAnnotationTaskBiz;
import cn.malgo.annotation.biz.task.ListAnnotationTaskBlockBiz;
import cn.malgo.annotation.biz.task.ListTaskDetailsBiz;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.task.ListAnnotationTaskBlockRequest;
import cn.malgo.annotation.request.task.ListAnnotationTaskRequest;
import cn.malgo.annotation.request.task.ListTaskDetailRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.vo.AnnotationTaskDetailVO;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/task")
public class AnnotationTaskController extends BaseController {

  private final ListAnnotationTaskBiz listAnnotationTaskBiz;
  private final ListAnnotationTaskBlockBiz listAnnotationTaskBlockBiz;
  private final ListTaskDetailsBiz listTaskDetailsBiz;

  public AnnotationTaskController(
      ListAnnotationTaskBiz listAnnotationTaskBiz,
      ListAnnotationTaskBlockBiz listAnnotationTaskBlockBiz,
      ListTaskDetailsBiz listTaskDetailsBiz) {
    this.listAnnotationTaskBiz = listAnnotationTaskBiz;
    this.listAnnotationTaskBlockBiz = listAnnotationTaskBlockBiz;
    this.listTaskDetailsBiz = listTaskDetailsBiz;
  }

  /** 查询任务列表 */
  @RequestMapping(value = "/list-annotation-tasks", method = RequestMethod.GET)
  public Response<PageVO<AnnotationTaskVO>> listAnnotationTask(
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount,
      ListAnnotationTaskRequest listAnnotationTaskRequest) {
    final Response result =
        new Response<>(
            listAnnotationTaskBiz.process(
                listAnnotationTaskRequest, 0, AnnotationRoleStateEnum.admin.getRole()));
    return result;
  }

  /** 查询任务详情列表 */
  @RequestMapping(value = "/list-task-details/{id}", method = RequestMethod.GET)
  public Response<AnnotationTaskDetailVO> listTaskDetails(
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount,
      @PathVariable("id") int id) {

    return new Response<>(
        listTaskDetailsBiz.process(
            new ListTaskDetailRequest(id), 0, AnnotationRoleStateEnum.admin.getRole()));
  }

  /** 查询任务block列表 */
  @RequestMapping(value = "/list-annotation-task-block", method = RequestMethod.GET)
  public Response<PageVO<AnnotationTaskBlock>> listAnnotationTaskBlock(
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount,
      ListAnnotationTaskBlockRequest listAnnotationTaskBlockRequest) {
    return new Response<>(
        listAnnotationTaskBlockBiz.process(
            listAnnotationTaskBlockRequest, 0, AnnotationRoleStateEnum.admin.getRole()));
  }
}
