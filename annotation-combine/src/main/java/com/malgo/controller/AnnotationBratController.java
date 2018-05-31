package com.malgo.controller;

import com.malgo.base.brat.GetAutoAnnotationBiz;
import com.malgo.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cjl on 2018/5/31.
 */
@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationBratController {


  private final GetAutoAnnotationBiz getAutoAnnotationBiz;

  public AnnotationBratController(GetAutoAnnotationBiz getAutoAnnotationBiz){
    this.getAutoAnnotationBiz=getAutoAnnotationBiz;
  }
  /**
   * 获取算法服务的预标注结果
   */
  @RequestMapping(value = "/get-auto-annotation/{id}",method = RequestMethod.GET)
  public Response getAutoAnnotation(@PathVariable int id){
    return new Response(getAutoAnnotationBiz.process(id,null));
  }
  /**
   * 新增标注过算法服务处理
   */
  @RequestMapping(value = "/add-annotation-algorithm",method = RequestMethod.POST)
  public Response addAnnotationAlgorithm(){
    return new Response("");
  }

  /**
   * 删除标注过算法服务处理
   */
  @RequestMapping(value = "/delete-annotation-algorithm",method = RequestMethod.POST)
  public Response deleteAnnotationAlgorithm(){
    return new Response("");
  }


  /**
   * 所有涉及到对标注entities处理的新增标注的接口，不过算法api
   */
  @RequestMapping(value = "/add-annotation",method = RequestMethod.POST)
  public Response addAnnotation(){
    return new Response("");
  }

  /**
   * 所有涉及到对entities处理的更新标注的接口，不过算法api
   */
  @RequestMapping(value = "/delete-annotation",method = RequestMethod.POST)
  public Response deleteAnnotation(){
    return new Response("");
  }

  /**
   * 所有涉及到对entities处理的更新标注的接口，不过算法api
   */
  @RequestMapping(value = "/update-annotation",method = RequestMethod.POST)
  public Response updateAnnotation(){
    return new Response("");
  }
  /**
   * 新增关联标注
   */
  @RequestMapping(value = "/add-relation",method = RequestMethod.POST)
  public Response addRelation(){
    return new Response("");
  }
  /**
   *  删除关联标注
   */
  @RequestMapping(value = "/delete-relation",method = RequestMethod.POST)
  public Response deleteRelation(){
    return new Response("");
  }
  /**
   * 更新关联标注
   */
  @RequestMapping(value = "/update-relation",method = RequestMethod.POST)
  public Response updateRelation(){
    return new Response("");
  }
}
