package com.malgo.controller;

import com.malgo.biz.brat.task.algorithm.AddAnnotationAlgorithmBiz;
import com.malgo.biz.brat.task.GetAutoAnnotationBiz;
import com.malgo.biz.brat.task.algorithm.DeleteAnnotationAlgorithmBiz;
import com.malgo.biz.brat.task.entities.AddAnnotationBiz;
import com.malgo.biz.brat.task.entities.DeleteAnnotationBiz;
import com.malgo.biz.brat.task.entities.UpdateAnnotationBiz;
import com.malgo.biz.brat.task.relations.AddRelationBiz;
import com.malgo.biz.brat.task.relations.DeleteRelationBiz;
import com.malgo.biz.brat.task.relations.UpdateRelationBiz;
import com.malgo.entity.UserAccount;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.request.brat.AddRelationRequest;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.request.brat.DeleteRelationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.request.brat.UpdateRelationRequest;
import com.malgo.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
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
public class AnnotationBratController extends BaseController{


  private final GetAutoAnnotationBiz getAutoAnnotationBiz;
  private final AddAnnotationAlgorithmBiz addAnnotationAlgorithmBiz;
  private final DeleteAnnotationAlgorithmBiz deleteAnnotationAlgorithmBiz;
  private final AddAnnotationBiz addAnnotationBiz;
  private final UpdateAnnotationBiz updateAnnotationBiz;
  private final DeleteAnnotationBiz deleteAnnotationBiz;
  private final AddRelationBiz addRelationBiz;
  private final UpdateRelationBiz updateRelationBiz;
  private final DeleteRelationBiz deleteRelationBiz;


  public AnnotationBratController(GetAutoAnnotationBiz getAutoAnnotationBiz,
      AddAnnotationAlgorithmBiz addAnnotationAlgorithmBiz,
      DeleteAnnotationAlgorithmBiz deleteAnnotationAlgorithmBiz,
      AddAnnotationBiz addAnnotationBiz,
      UpdateAnnotationBiz updateAnnotationBiz,
      DeleteAnnotationBiz deleteAnnotationBiz,
      AddRelationBiz addRelationBiz,
      UpdateRelationBiz updateRelationBiz,
      DeleteRelationBiz deleteRelationBiz
  ) {
    this.getAutoAnnotationBiz = getAutoAnnotationBiz;
    this.addAnnotationAlgorithmBiz = addAnnotationAlgorithmBiz;
    this.deleteAnnotationAlgorithmBiz=deleteAnnotationAlgorithmBiz;
    this.addAnnotationBiz=addAnnotationBiz;
    this.updateAnnotationBiz=updateAnnotationBiz;
    this.deleteAnnotationBiz=deleteAnnotationBiz;
    this.addRelationBiz=addRelationBiz;
    this.updateRelationBiz=updateRelationBiz;
    this.deleteRelationBiz=deleteRelationBiz;
  }

  /**
   * 获取算法服务的预标注结果
   */
  @RequestMapping(value = "/get-auto-annotation/{id}", method = RequestMethod.GET)
  public Response getAutoAnnotation(@PathVariable int id) {
    return new Response(getAutoAnnotationBiz.process(id, 0,0));
  }

  /**
   * 新增标注,经过算法服务处理
   */
  @RequestMapping(value = "/add-annotation-algorithm", method = RequestMethod.POST)
  public Response addAnnotationAlgorithm(AddAnnotationRequest addAnnotationRequest,@ModelAttribute("userAccount") UserAccount userAccount) {
    return new Response(addAnnotationAlgorithmBiz.process(addAnnotationRequest, userAccount.getId(),userAccount.getRoleId()));
  }

  /**
   * 标注entities处理，新增标注的接口，不过算法api
   */
  @RequestMapping(value = "/add-annotation", method = RequestMethod.POST)
  public Response addReviewAnnotation(AddAnnotationRequest addAnnotationRequest,@ModelAttribute("userAccount") UserAccount userAccount) {
    return new Response(addAnnotationBiz.process(addAnnotationRequest,userAccount.getId(),userAccount.getRoleId()));
  }

  /**
   * entities处理，更新标注 ，不过算法api
   */
  @RequestMapping(value = "/update-annotation")
  public Response updateReviewAnnotation(UpdateAnnotationRequest updateAnnotationRequest,@ModelAttribute("userAccount") UserAccount userAccount){
    return new Response(updateAnnotationBiz.process(updateAnnotationRequest,userAccount.getId(),userAccount.getRoleId()));
  }

  /**
   * entities处理，删除标注，不过算法api
   */
  @RequestMapping(value = "/delete-annotation",method = RequestMethod.POST)
  public Response deleteReviewAnnotation(DeleteAnnotationRequest deleteAnnotationRequest,@ModelAttribute("userAccount") UserAccount userAccount){
    return new Response(deleteAnnotationBiz.process(deleteAnnotationRequest,userAccount.getId(),userAccount.getRoleId()));
  }

  /**
   * 普通人员删除标注，通过算法服务处理
   */
  @RequestMapping(value = "/delete-annotation-algorithm", method = RequestMethod.POST)
  public Response deleteAnnotationAlgorithm(DeleteAnnotationRequest deleteAnnotationRequest,@ModelAttribute("userAccount") UserAccount userAccount) {
    return new Response(deleteAnnotationAlgorithmBiz.process(deleteAnnotationRequest,userAccount.getId(),userAccount.getRoleId()));
  }
  /**
   * 普通人员，新增关联标注
   */
  @RequestMapping(value = "add-relation",method = RequestMethod.POST)
  public Response addRelation(AddRelationRequest addRelationRequest,@ModelAttribute("userAccount") UserAccount userAccount ){
    return new Response(addRelationBiz.process(addRelationRequest,userAccount.getId(),userAccount.getRoleId()));
  }

  /**
   * 审核人员 删除关联标注
   */
  @RequestMapping(value = "/delete-relation", method = RequestMethod.POST)
  public Response deleteRelation(DeleteRelationRequest deleteRelationRequest,@ModelAttribute("userAccount") UserAccount userAccount) {
    return new Response(deleteRelationBiz.process(deleteRelationRequest,userAccount.getId(),userAccount.getRoleId()));
  }

  /**
   * 更新关联标注
   */
  @RequestMapping(value = "/update-relation", method = RequestMethod.POST)
  public Response updateRelation(UpdateRelationRequest updateRelationRequest,@ModelAttribute("userAccount") UserAccount userAccount) {
    return new Response(updateRelationBiz.process(updateRelationRequest,userAccount.getId(),userAccount.getRoleId()));
  }
}
