package cn.malgo.annotation.controller.task;

import cn.malgo.annotation.biz.GetDoingTaskSummaryInfoBiz;
import cn.malgo.annotation.biz.task.AddBlocksToTaskBiz;
import cn.malgo.annotation.biz.task.CreateTaskBiz;
import cn.malgo.annotation.biz.task.GetUnCoveredBlockBiz;
import cn.malgo.annotation.biz.task.ListAnnotationTaskBiz;
import cn.malgo.annotation.biz.task.ListAnnotationTaskBlockBiz;
import cn.malgo.annotation.biz.task.ListTaskDetailsBiz;
import cn.malgo.annotation.biz.task.RefreshTaskSummaryBiz;
import cn.malgo.annotation.biz.task.TerminateTaskBiz;
import cn.malgo.annotation.controller.BaseController;
import cn.malgo.annotation.request.task.AddBlocksToTaskRequest;
import cn.malgo.annotation.request.task.CreateTaskRequest;
import cn.malgo.annotation.request.task.GetUnCoveredBlockRequest;
import cn.malgo.annotation.request.task.ListAnnotationTaskBlockRequest;
import cn.malgo.annotation.request.task.ListAnnotationTaskRequest;
import cn.malgo.annotation.request.task.ListTaskDetailRequest;
import cn.malgo.annotation.request.task.TerminateTaskRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationTaskBlockResponse;
import cn.malgo.annotation.vo.AnnotationTaskDetailVO;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.annotation.vo.TaskInfoVO;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/task")
public class AnnotationTaskController extends BaseController {

  private final CreateTaskBiz createTaskBiz;
  private final AddBlocksToTaskBiz addBlocksToTaskBiz;
  private final ListAnnotationTaskBiz listAnnotationTaskBiz;
  private final ListAnnotationTaskBlockBiz listAnnotationTaskBlockBiz;
  private final ListTaskDetailsBiz listTaskDetailsBiz;
  private final TerminateTaskBiz terminateTaskBiz;
  private final RefreshTaskSummaryBiz refreshTaskSummaryBiz;
  private final GetUnCoveredBlockBiz getUnCoveredBlockBiz;
  private final GetDoingTaskSummaryInfoBiz getDoingTaskSummaryInfoBiz;

  public AnnotationTaskController(
      final CreateTaskBiz createTaskBiz,
      final AddBlocksToTaskBiz addBlocksToTaskBiz,
      final ListAnnotationTaskBiz listAnnotationTaskBiz,
      final ListAnnotationTaskBlockBiz listAnnotationTaskBlockBiz,
      final ListTaskDetailsBiz listTaskDetailsBiz,
      final TerminateTaskBiz terminateTaskBiz,
      final RefreshTaskSummaryBiz refreshTaskSummaryBiz,
      final GetUnCoveredBlockBiz getUnCoveredBlockBiz,
      final GetDoingTaskSummaryInfoBiz getDoingTaskSummaryInfoBiz) {
    this.createTaskBiz = createTaskBiz;
    this.addBlocksToTaskBiz = addBlocksToTaskBiz;
    this.listAnnotationTaskBiz = listAnnotationTaskBiz;
    this.listAnnotationTaskBlockBiz = listAnnotationTaskBlockBiz;
    this.listTaskDetailsBiz = listTaskDetailsBiz;
    this.terminateTaskBiz = terminateTaskBiz;
    this.refreshTaskSummaryBiz = refreshTaskSummaryBiz;
    this.getUnCoveredBlockBiz = getUnCoveredBlockBiz;
    this.getDoingTaskSummaryInfoBiz = getDoingTaskSummaryInfoBiz;
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

  /** 查询任务列表 */
  @RequestMapping(value = "/list-annotation-tasks", method = RequestMethod.GET)
  public Response<PageVO<AnnotationTaskVO>> listAnnotationTask(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      ListAnnotationTaskRequest request) {
    return new Response<>(listAnnotationTaskBiz.process(request, userAccount));
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

  /** 结束任务 */
  @RequestMapping(value = "/terminate-task", method = RequestMethod.POST)
  public Response terminateTask(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody TerminateTaskRequest terminateTaskRequest) {
    return new Response<>(terminateTaskBiz.process(terminateTaskRequest, userAccount));
  }

  /** 未覆盖度语料查询 */
  @RequestMapping(value = "/get-un-covered-block", method = RequestMethod.GET)
  public Response<List<AnnotationTaskBlockResponse>> getUnCoveredBlocks(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      GetUnCoveredBlockRequest getUnCoveredBlockRequest) {
    return new Response<>(getUnCoveredBlockBiz.process(getUnCoveredBlockRequest, userAccount));
  }

  @RequestMapping(value = "/refresh-task-summary", method = RequestMethod.POST)
  public Response<AnnotationTaskVO> refreshTaskSummary(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody TerminateTaskRequest terminateTaskRequest) {
    return new Response<>(refreshTaskSummaryBiz.process(terminateTaskRequest, userAccount));
  }

  /** 标注人员未结束批次统计数据查询 */
  @RequestMapping(value = "/get-doing-task-summary", method = RequestMethod.GET)
  public Response<TaskInfoVO> getDoingTaskSummary(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(getDoingTaskSummaryInfoBiz.process(null, userAccount));
  }
}
