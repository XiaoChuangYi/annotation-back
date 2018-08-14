package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.CountAnnotationBiz;
import cn.malgo.annotation.biz.DesignateAnnotationBiz;
import cn.malgo.annotation.biz.GetAnnotationSummaryBiz;
import cn.malgo.annotation.biz.GetUnDistributedAnnotationWordNumBiz;
import cn.malgo.annotation.biz.ListAnnotationBiz;
import cn.malgo.annotation.biz.OneKeyDesignateAnnotationBiz;
import cn.malgo.annotation.biz.RandomDesignateAnnotationBiz;
import cn.malgo.annotation.biz.brat.AnnotationExamineResetBiz;
import cn.malgo.annotation.biz.brat.ListAnTypeBiz;
import cn.malgo.annotation.biz.brat.PreAnnotationRecycleBiz;
import cn.malgo.annotation.biz.task.OneKeyAddBlocksToTaskBiz;
import cn.malgo.annotation.request.AnnotationRecycleRequest;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.request.CountAnnotationRequest;
import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.request.ListAnnotationRequest;
import cn.malgo.annotation.request.OneKeyDesignateAnnotationRequest;
import cn.malgo.annotation.request.RandomDesignateAnnotationRequest;
import cn.malgo.annotation.request.task.OneKeyAddBlocksToTaskRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnTypeVO;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationController extends BaseController {

  private final ListAnnotationBiz listAnnotationBiz;
  private final DesignateAnnotationBiz designateAnnotationBiz;
  private final GetAnnotationSummaryBiz getAnnotationSummaryBiz;
  private final RandomDesignateAnnotationBiz randomDesignateAnnotationBiz;
  private final CountAnnotationBiz countAnnotationBiz;
  private final ListAnTypeBiz listAnTypeBiz;
  private final PreAnnotationRecycleBiz preAnnotationRecycleBiz;
  private final AnnotationExamineResetBiz annotationExamineResetBiz;
  private final OneKeyDesignateAnnotationBiz oneKeyDesignateAnnotationBiz;
  private final OneKeyAddBlocksToTaskBiz oneKeyAddBlocksToTaskBiz;
  private final GetUnDistributedAnnotationWordNumBiz getUnDistributedAnnotationWordNumBiz;

  public AnnotationController(
      final ListAnnotationBiz listAnnotationBiz,
      final DesignateAnnotationBiz designateAnnotationBiz,
      final GetAnnotationSummaryBiz getAnnotationSummaryBiz,
      final RandomDesignateAnnotationBiz randomDesignateAnnotationBiz,
      final CountAnnotationBiz countAnnotationBiz,
      final ListAnTypeBiz listAnTypeBiz,
      final PreAnnotationRecycleBiz preAnnotationRecycleBiz,
      final AnnotationExamineResetBiz annotationExamineResetBiz,
      final OneKeyDesignateAnnotationBiz oneKeyDesignateAnnotationBiz,
      final OneKeyAddBlocksToTaskBiz oneKeyAddBlocksToTaskBiz,
      final GetUnDistributedAnnotationWordNumBiz getUnDistributedAnnotationWordNumBiz) {
    this.listAnnotationBiz = listAnnotationBiz;
    this.designateAnnotationBiz = designateAnnotationBiz;
    this.getAnnotationSummaryBiz = getAnnotationSummaryBiz;
    this.randomDesignateAnnotationBiz = randomDesignateAnnotationBiz;
    this.countAnnotationBiz = countAnnotationBiz;
    this.listAnTypeBiz = listAnTypeBiz;
    this.preAnnotationRecycleBiz = preAnnotationRecycleBiz;
    this.annotationExamineResetBiz = annotationExamineResetBiz;
    this.oneKeyDesignateAnnotationBiz = oneKeyDesignateAnnotationBiz;
    this.oneKeyAddBlocksToTaskBiz = oneKeyAddBlocksToTaskBiz;
    this.getUnDistributedAnnotationWordNumBiz = getUnDistributedAnnotationWordNumBiz;
  }

  /** 条件，分页查询annotation列表 */
  @RequestMapping(value = "/list-annotation", method = RequestMethod.GET)
  public Response<PageVO<AnnotationBratVO>> listAnnotationCombine(
      ListAnnotationRequest request,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(listAnnotationBiz.process(request, userAccount));
  }

  /** 根据Annotation的idList，以及用户id，批量指派给特定的用户 */
  @RequestMapping(value = "/designate-task-annotation", method = RequestMethod.POST)
  public Response designateTaskAnnotation(
      @RequestBody DesignateAnnotationRequest designateAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(designateAnnotationBiz.process(designateAnnotationRequest, userAccount));
  }

  /** 根据用户Id集合userIdList,以及设定的随机指派数num，标注类型列表annotationTypes */
  @RequestMapping(value = "/random-designate-task-annotation", method = RequestMethod.POST)
  public Response randomDesignateTaskAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody RandomDesignateAnnotationRequest randomDesignateAnnotationRequest) {
    return new Response<>(
        randomDesignateAnnotationBiz.process(randomDesignateAnnotationRequest, userAccount));
  }

  /** 标注预览(标注各种状态下的条数图形化展示) */
  @RequestMapping(value = "/get-annotation-summary", method = RequestMethod.GET)
  public Response getAnnotationSummary(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response<>(getAnnotationSummaryBiz.process(null, userAccount));
  }

  /** 根据标注类型，返回指定标注类型的未分配的总条数 */
  @RequestMapping(value = "/count-undistributed-annotation", method = RequestMethod.GET)
  public Response countUnDistributedAnnotation(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      CountAnnotationRequest countAnnotationRequest) {
    return new Response<>(countAnnotationBiz.process(countAnnotationRequest, userAccount));
  }

  /** 查询分词标注类型列表 */
  @RequestMapping(value = "/list-type", method = RequestMethod.GET)
  public Response<List<AnTypeVO>> listType() {
    return new Response<>(listAnTypeBiz.process(null, null));
  }

  /** 待标注/任务回收功能 */
  @RequestMapping(value = "/annotation-recycle", method = RequestMethod.POST)
  public Response annotationRecycle(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody AnnotationRecycleRequest annotationRecycleRequest) {
    return new Response<>(preAnnotationRecycleBiz.process(annotationRecycleRequest, userAccount));
  }

  /** 审核放弃 */
  @RequestMapping(value = "/annotation-examine-abandon", method = RequestMethod.POST)
  public Response<AnnotationBratVO> annotationExamineAbandon(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody AnnotationStateRequest annotationStateRequest) {
    return new Response<>(annotationExamineResetBiz.process(annotationStateRequest, userAccount));
  }

  /** 一键指派标注 */
  @RequestMapping(value = "/one-key-designate", method = RequestMethod.POST)
  public Response oneKeyDesignate(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody OneKeyDesignateAnnotationRequest request) {
    return new Response<>(oneKeyDesignateAnnotationBiz.process(request, userAccount));
  }

  /** 一键添加语料到批次 */
  @RequestMapping(value = "/one-key-add-blocks-to-task", method = RequestMethod.POST)
  public Response oneKeyAddBlocksToTask(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount,
      @RequestBody OneKeyAddBlocksToTaskRequest request) {
    return new Response<>(oneKeyAddBlocksToTaskBiz.process(request, userAccount));
  }

  /** 获取未指派语料总字数 */
  @RequestMapping(value = "/get-un-distributed-word-num", method = RequestMethod.GET)
  public Response getUnDistributedWordNum(
      @ModelAttribute(value = "userAccount", binding = false) UserDetails userAccount) {
    return new Response(getUnDistributedAnnotationWordNumBiz.process(null, userAccount));
  }
}
