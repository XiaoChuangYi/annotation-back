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
import cn.malgo.annotation.config.PermissionConstant;
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
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.common.auth.user.UserDetailService;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/task")
public class AnnotationTaskController {

  private final CreateTaskBiz createTaskBiz;
  private final AddBlocksToTaskBiz addBlocksToTaskBiz;
  private final ListAnnotationTaskBiz listAnnotationTaskBiz;
  private final ListAnnotationTaskBlockBiz listAnnotationTaskBlockBiz;
  private final ListTaskDetailsBiz listTaskDetailsBiz;
  private final TerminateTaskBiz terminateTaskBiz;
  private final RefreshTaskSummaryBiz refreshTaskSummaryBiz;
  private final GetUnCoveredBlockBiz getUnCoveredBlockBiz;
  private final GetDoingTaskSummaryInfoBiz getDoingTaskSummaryInfoBiz;

  private final UserDetailService userDetailService;

  public AnnotationTaskController(
      final CreateTaskBiz createTaskBiz,
      final AddBlocksToTaskBiz addBlocksToTaskBiz,
      final ListAnnotationTaskBiz listAnnotationTaskBiz,
      final ListAnnotationTaskBlockBiz listAnnotationTaskBlockBiz,
      final ListTaskDetailsBiz listTaskDetailsBiz,
      final TerminateTaskBiz terminateTaskBiz,
      final RefreshTaskSummaryBiz refreshTaskSummaryBiz,
      final GetUnCoveredBlockBiz getUnCoveredBlockBiz,
      final GetDoingTaskSummaryInfoBiz getDoingTaskSummaryInfoBiz,
      final UserDetailService userDetailService) {

    this.createTaskBiz = createTaskBiz;
    this.addBlocksToTaskBiz = addBlocksToTaskBiz;
    this.listAnnotationTaskBiz = listAnnotationTaskBiz;
    this.listAnnotationTaskBlockBiz = listAnnotationTaskBlockBiz;
    this.listTaskDetailsBiz = listTaskDetailsBiz;
    this.terminateTaskBiz = terminateTaskBiz;
    this.refreshTaskSummaryBiz = refreshTaskSummaryBiz;
    this.getUnCoveredBlockBiz = getUnCoveredBlockBiz;
    this.getDoingTaskSummaryInfoBiz = getDoingTaskSummaryInfoBiz;
    this.userDetailService = userDetailService;
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_BATCH_INSERT)
  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public Response<AnnotationTaskVO> create(
      @RequestBody CreateTaskRequest request) {
    return new Response<>(createTaskBiz.process(request, null));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_INSERT)
  @RequestMapping(value = "/add-blocks-to-task", method = RequestMethod.POST)
  public Response addBlocksToTask(
      @RequestBody AddBlocksToTaskRequest addBlocksToTaskRequest) {
    return new Response<>(addBlocksToTaskBiz.process(addBlocksToTaskRequest, null));
  }

  /**
   * 查询任务列表
   */
  @PermissionAnno(PermissionConstant.ANNOTATION_BATCH_LIST)
  @RequestMapping(value = "/list-annotation-tasks", method = RequestMethod.GET)
  public Response<PageVO<AnnotationTaskVO>> listAnnotationTask(
      ListAnnotationTaskRequest request) {
    return new Response<>(listAnnotationTaskBiz.process(request, null));
  }

  /**
   * 查询任务详情列表
   */
  @PermissionAnno(PermissionConstant.ANNOTATION_BATCH_DETAILS)
  @RequestMapping(value = "/list-task-details/{id}", method = RequestMethod.GET)
  public Response<AnnotationTaskDetailVO> listTaskDetails(
      @PathVariable("id") long id) {
    return new Response<>(listTaskDetailsBiz.process(new ListTaskDetailRequest(id), null));
  }

  /**
   * 查询任务block列表
   */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_LIST)
  @RequestMapping(value = "/list-annotation-task-block", method = RequestMethod.GET)
  public Response<PageVO<AnnotationTaskBlockResponse>> listAnnotationTaskBlock(
      ListAnnotationTaskBlockRequest listAnnotationTaskBlockRequest) {
    return new Response<>(
        listAnnotationTaskBlockBiz.process(listAnnotationTaskBlockRequest, null));
  }

  /**
   * 结束任务
   */
  @PermissionAnno(PermissionConstant.ANNOTATION_BATCH_TERMINATE)
  @RequestMapping(value = "/terminate-task", method = RequestMethod.POST)
  public Response terminateTask(
      @RequestBody TerminateTaskRequest terminateTaskRequest) {
    return new Response<>(terminateTaskBiz.process(terminateTaskRequest, null));
  }

  /**
   * 未覆盖度语料查询
   */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_UN_COVERAGE)
  @RequestMapping(value = "/get-un-covered-block", method = RequestMethod.GET)
  public Response<List<AnnotationTaskBlockResponse>> getUnCoveredBlocks(
      GetUnCoveredBlockRequest getUnCoveredBlockRequest) {
    return new Response<>(getUnCoveredBlockBiz.process(getUnCoveredBlockRequest, null));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_SUMMARY_TASK_REFRESH)
  @RequestMapping(value = "/refresh-task-summary", method = RequestMethod.POST)
  public Response<AnnotationTaskVO> refreshTaskSummary(
      @RequestBody TerminateTaskRequest terminateTaskRequest) {
    return new Response<>(refreshTaskSummaryBiz.process(terminateTaskRequest, null));
  }

  /**
   * 标注人员未结束批次统计数据查询
   */
  @PermissionAnno(PermissionConstant.ANNOTATION_SUMMARY_DOING_TASK)
  @RequestMapping(value = "/get-doing-task-summary", method = RequestMethod.GET)
  public Response<TaskInfoVO> getDoingTaskSummary(
      final HttpServletRequest request) {
    final UserDetails userDetails = new UserDetails() {
      @Override
      public long getId() {
        return userDetailService.getUserDetails(request).getId();
      }

      @Override
      public boolean hasPermission(String permission) {
        return userDetailService.getUserDetails(request).hasPermission(permission);
      }
    };
    return new Response<>(getDoingTaskSummaryInfoBiz.process(null, userDetails));
  }
}
