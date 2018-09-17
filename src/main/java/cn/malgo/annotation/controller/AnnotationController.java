package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.DesignateAnnotationBiz;
import cn.malgo.annotation.biz.GetUnDistributedAnnotationWordNumBiz;
import cn.malgo.annotation.biz.ListAnnotationBiz;
import cn.malgo.annotation.biz.OneKeyDesignateAnnotationBiz;
import cn.malgo.annotation.biz.brat.ListAnTypeBiz;
import cn.malgo.annotation.biz.brat.PreAnnotationRecycleBiz;
import cn.malgo.annotation.biz.brat.task.BatchDeleteAnnotationBratTypeBiz;
import cn.malgo.annotation.biz.task.OneKeyAddBlocksToTaskBiz;
import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.request.AnnotationRecycleRequest;
import cn.malgo.annotation.request.anno.BatchDeleteAnnotationBratTypeRequest;
import cn.malgo.annotation.request.anno.DesignateAnnotationRequest;
import cn.malgo.annotation.request.anno.GetUnDistributedAnnotationRequest;
import cn.malgo.annotation.request.anno.ListAnnotationRequest;
import cn.malgo.annotation.request.OneKeyDesignateAnnotationRequest;
import cn.malgo.annotation.request.task.OneKeyAddBlocksToTaskRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnTypeVO;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.common.auth.user.UserDetailService;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationController {

  private final ListAnnotationBiz listAnnotationBiz;
  private final DesignateAnnotationBiz designateAnnotationBiz;
  private final ListAnTypeBiz listAnTypeBiz;
  private final PreAnnotationRecycleBiz preAnnotationRecycleBiz;
  private final OneKeyDesignateAnnotationBiz oneKeyDesignateAnnotationBiz;
  private final OneKeyAddBlocksToTaskBiz oneKeyAddBlocksToTaskBiz;
  private final GetUnDistributedAnnotationWordNumBiz getUnDistributedAnnotationWordNumBiz;
  private final UserDetailService userDetailService;

  private final BatchDeleteAnnotationBratTypeBiz batchDeleteAnnotationBratTypeBiz;

  public AnnotationController(
      final ListAnnotationBiz listAnnotationBiz,
      final DesignateAnnotationBiz designateAnnotationBiz,
      final ListAnTypeBiz listAnTypeBiz,
      final PreAnnotationRecycleBiz preAnnotationRecycleBiz,
      final OneKeyDesignateAnnotationBiz oneKeyDesignateAnnotationBiz,
      final OneKeyAddBlocksToTaskBiz oneKeyAddBlocksToTaskBiz,
      final GetUnDistributedAnnotationWordNumBiz getUnDistributedAnnotationWordNumBiz,
      final UserDetailService userDetailService,
      final BatchDeleteAnnotationBratTypeBiz batchDeleteAnnotationBratTypeBiz) {
    this.listAnnotationBiz = listAnnotationBiz;
    this.designateAnnotationBiz = designateAnnotationBiz;
    this.listAnTypeBiz = listAnTypeBiz;
    this.preAnnotationRecycleBiz = preAnnotationRecycleBiz;
    this.oneKeyDesignateAnnotationBiz = oneKeyDesignateAnnotationBiz;
    this.oneKeyAddBlocksToTaskBiz = oneKeyAddBlocksToTaskBiz;
    this.getUnDistributedAnnotationWordNumBiz = getUnDistributedAnnotationWordNumBiz;
    this.userDetailService = userDetailService;
    this.batchDeleteAnnotationBratTypeBiz = batchDeleteAnnotationBratTypeBiz;
  }

  /** 条件，分页查询annotation列表 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_LIST)
  @RequestMapping(value = "/list-annotation", method = RequestMethod.GET)
  public Response<PageVO<AnnotationBratVO>> listAnnotationCombine(
      ListAnnotationRequest request, final HttpServletRequest servletRequest) {
    final UserDetails userDetails =
        new UserDetails() {
          @Override
          public long getId() {
            return userDetailService.getUserDetails(servletRequest).getId();
          }

          @Override
          public boolean hasPermission(String permission) {
            return userDetailService.getUserDetails(servletRequest).hasPermission(permission);
          }
        };
    return new Response<>(listAnnotationBiz.process(request, userDetails));
  }

  /** 根据Annotation的idList，以及用户id，批量指派给特定的用户 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_DESIGNATE)
  @RequestMapping(value = "/designate-task-annotation", method = RequestMethod.POST)
  public Response designateTaskAnnotation(
      @RequestBody DesignateAnnotationRequest designateAnnotationRequest) {
    return new Response<>(designateAnnotationBiz.process(designateAnnotationRequest, null));
  }

  /** 查询分词标注类型列表 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TYPE_LIST)
  @RequestMapping(value = "/list-type", method = RequestMethod.GET)
  public Response<List<AnTypeVO>> listType() {
    return new Response<>(listAnTypeBiz.process(null, null));
  }

  /** 待标注/任务回收功能 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_RECYCLE)
  @RequestMapping(value = "/annotation-recycle", method = RequestMethod.POST)
  public Response annotationRecycle(
      @RequestBody AnnotationRecycleRequest annotationRecycleRequest) {
    return new Response<>(preAnnotationRecycleBiz.process(annotationRecycleRequest, null));
  }

  /** 一键指派标注 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_DESIGNATE)
  @RequestMapping(value = "/one-key-designate", method = RequestMethod.POST)
  public Response oneKeyDesignate(@RequestBody OneKeyDesignateAnnotationRequest request) {
    return new Response<>(oneKeyDesignateAnnotationBiz.process(request, null));
  }

  /** 一键添加语料到批次 */
  @PermissionAnno(PermissionConstant.ANNOTATION_BLOCK_INSERT)
  @RequestMapping(value = "/one-key-add-blocks-to-task", method = RequestMethod.POST)
  public Response oneKeyAddBlocksToTask(@RequestBody OneKeyAddBlocksToTaskRequest request) {
    return new Response<>(oneKeyAddBlocksToTaskBiz.process(request, null));
  }

  /** 获取未指派语料总字数 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_UNDISTRIBUTED_WORD_NUM)
  @RequestMapping(value = "/get-un-distributed-word-num", method = RequestMethod.GET)
  public Response getUnDistributedWordNum(GetUnDistributedAnnotationRequest request) {
    return new Response(getUnDistributedAnnotationWordNumBiz.process(request, null));
  }

  /** 批量删除标注标签 */
  @RequestMapping(value = "/batch-delete-annotation-type", method = RequestMethod.POST)
  public Response batchDeleteAnnotationType(
      @RequestBody BatchDeleteAnnotationBratTypeRequest request) {
    return new Response(batchDeleteAnnotationBratTypeBiz.process(request, null));
  }
}
