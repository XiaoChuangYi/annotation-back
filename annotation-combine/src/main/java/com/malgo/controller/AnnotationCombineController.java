package com.malgo.controller;

import com.malgo.biz.CountAnnotationBiz;
import com.malgo.biz.DesignateAnnotationBiz;
import com.malgo.biz.GetAnnotationSummaryBiz;
import com.malgo.biz.GetAnnotationSummaryByAssigneeBiz;
import com.malgo.biz.ListAnnotationBiz;
import com.malgo.biz.RandomDesignateAnnotationBiz;
import com.malgo.biz.brat.ListAnTypeBiz;
import com.malgo.entity.UserAccount;
import com.malgo.request.CountAnnotationRequest;
import com.malgo.request.DesignateAnnotationRequest;
import com.malgo.request.ListAnnotationCombineRequest;
import com.malgo.request.RandomDesignateAnnotationRequest;
import com.malgo.request.SetUserStateRequest;
import com.malgo.result.PageVO;
import com.malgo.result.Response;
import com.malgo.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cjl on 2018/5/30.
 */
@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationCombineController extends BaseController{

  private final ListAnnotationBiz listAnnotationBiz;
  private final DesignateAnnotationBiz designateAnnotationBiz;
  private final GetAnnotationSummaryBiz getAnnotationSummaryBiz;
  private final GetAnnotationSummaryByAssigneeBiz getAnnotationSummaryByAssigneeBiz;
  private final RandomDesignateAnnotationBiz randomDesignateAnnotationBiz;
  private final CountAnnotationBiz countAnnotationBiz;
  private final ListAnTypeBiz listAnTypeBiz;


  public AnnotationCombineController(ListAnnotationBiz listAnnotationBiz,
      DesignateAnnotationBiz designateAnnotationBiz,
      GetAnnotationSummaryBiz getAnnotationSummaryBiz,
      GetAnnotationSummaryByAssigneeBiz getAnnotationSummaryByAssigneeBiz,
      RandomDesignateAnnotationBiz randomDesignateAnnotationBiz,
      CountAnnotationBiz countAnnotationBiz,
      ListAnTypeBiz listAnTypeBiz){
    this.listAnnotationBiz=listAnnotationBiz;
    this.designateAnnotationBiz=designateAnnotationBiz;
    this.getAnnotationSummaryBiz=getAnnotationSummaryBiz;
    this.randomDesignateAnnotationBiz=randomDesignateAnnotationBiz;
    this.countAnnotationBiz=countAnnotationBiz;
    this.listAnTypeBiz=listAnTypeBiz;
    this.getAnnotationSummaryByAssigneeBiz=getAnnotationSummaryByAssigneeBiz;
  }

  /**
   * 条件，分页查询annotation列表
   */
  @RequestMapping(value = "/list-annotation",method = RequestMethod.GET)
  public Response<PageVO<AnnotationCombineBratVO>> listAnnotationCombine(ListAnnotationCombineRequest annotationCombineQuery){
      return new Response(listAnnotationBiz.process(annotationCombineQuery,0,0));
  }

  /**
   * 根据Annotation的idList，以及用户id，批量指派给特定的用户
   */
  @RequestMapping(value = "/designate-task-annotation",method = RequestMethod.POST)
  public Response designateTaskAnnotation(@RequestBody DesignateAnnotationRequest designateAnnotationRequest,
      @ModelAttribute("userAccount") UserAccount userAccount){
    return new Response(designateAnnotationBiz.process(designateAnnotationRequest,userAccount.getId(),userAccount.getRoleId()));
  }

  /**
   * 根据用户Id集合userIdList,以及设定的随机指派数num，标注类型列表annotationTypes
   */
  @RequestMapping(value = "/random-designate-task-annotation",method =RequestMethod.POST)
  public Response randomDesignateTaskAnnotation(@RequestBody RandomDesignateAnnotationRequest randomDesignateAnnotationRequest){
    return new Response(randomDesignateAnnotationBiz.process(randomDesignateAnnotationRequest,0,0));
  }

  /**
   * 标注预览(标注各种状态下的条数图形化展示)
   */
  @RequestMapping(value = "get-annotation-summary",method = RequestMethod.GET)
  public Response getAnnotationSummary(){
    return new Response(getAnnotationSummaryBiz.process(null,0,0));
  }

  /**
   * 根据标注类型，返回指定标注类型的未分配的总条数
   */
  @RequestMapping(value = "count-undistributed-annotation",method = RequestMethod.GET)
  public Response countUnDistributedAnnotation(CountAnnotationRequest countAnnotationRequest){
    return new Response(countAnnotationBiz.process(countAnnotationRequest,0,0));
  }

  /**
   * 查询分词标注类型列表
   */
  @RequestMapping(value = "list-type",method = RequestMethod.GET)
  public Response listType(){
    return new Response(listAnTypeBiz.process(null,0,0));
  }

  /**
   * 根据被指派人userId,查询其标注的各种状态
   */
  @RequestMapping(value = "get-annotation-summary-by-assignee",method = RequestMethod.GET)
  public Response getAnnotationSummaryByAssignee(SetUserStateRequest setUserStateRequest,@ModelAttribute("userAccount") UserAccount userAccount){
    return new Response(getAnnotationSummaryByAssigneeBiz.process(setUserStateRequest,userAccount.getId(),userAccount.getRoleId()));
  }
}
