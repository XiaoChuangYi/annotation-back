package com.malgo.controller;

import com.malgo.biz.brat.task.GetAutoAnnotationBiz;
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
import com.malgo.request.brat.GetAutoAnnotationRequest;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.request.brat.UpdateRelationRequest;
import com.malgo.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Created by cjl on 2018/5/31. */
@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationBratController extends BaseController {

  private final GetAutoAnnotationBiz getAutoAnnotationBiz;
  private final AddAnnotationBiz addAnnotationBiz;
  private final UpdateAnnotationBiz updateAnnotationBiz;
  private final DeleteAnnotationBiz deleteAnnotationBiz;
  private final AddRelationBiz addRelationBiz;
  private final UpdateRelationBiz updateRelationBiz;
  private final DeleteRelationBiz deleteRelationBiz;

  public AnnotationBratController(
      GetAutoAnnotationBiz getAutoAnnotationBiz,
      AddAnnotationBiz addAnnotationBiz,
      UpdateAnnotationBiz updateAnnotationBiz,
      DeleteAnnotationBiz deleteAnnotationBiz,
      AddRelationBiz addRelationBiz,
      UpdateRelationBiz updateRelationBiz,
      DeleteRelationBiz deleteRelationBiz) {
    this.getAutoAnnotationBiz = getAutoAnnotationBiz;
    this.addAnnotationBiz = addAnnotationBiz;
    this.updateAnnotationBiz = updateAnnotationBiz;
    this.deleteAnnotationBiz = deleteAnnotationBiz;
    this.addRelationBiz = addRelationBiz;
    this.updateRelationBiz = updateRelationBiz;
    this.deleteRelationBiz = deleteRelationBiz;
  }

  /** 获取算法服务的预标注结果 */
  @RequestMapping(value = "/get-auto-annotation", method = RequestMethod.GET)
  public Response getAutoAnnotation(
      @RequestParam("id") int id,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response(
        getAutoAnnotationBiz.process(
            new GetAutoAnnotationRequest(id), userAccount.getId(), userAccount.getRoleId()));
  }
  /** 标注entities处理，新增标注的接口，不过算法api */
  @RequestMapping(value = "/add-annotation", method = RequestMethod.POST)
  public Response addAnnotation(
      @RequestBody AddAnnotationRequest addAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response(
        addAnnotationBiz.process(
            addAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }
  /** entities处理，更新标注 ，不过算法api */
  @RequestMapping(value = "/update-annotation", method = RequestMethod.POST)
  public Response updateAnnotation(
      @RequestBody UpdateAnnotationRequest updateAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response(
        updateAnnotationBiz.process(
            updateAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }
  /** entities处理，删除标注，不过算法api */
  @RequestMapping(value = "/delete-annotation", method = RequestMethod.POST)
  public Response deleteAnnotation(
      @RequestBody DeleteAnnotationRequest deleteAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response(
        deleteAnnotationBiz.process(
            deleteAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }
  /** 普通人员，新增关联标注 */
  @RequestMapping(value = "add-relation", method = RequestMethod.POST)
  public Response addRelation(
      @RequestBody AddRelationRequest addRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response(
        addRelationBiz.process(addRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 审核人员 删除关联标注 */
  @RequestMapping(value = "/delete-relation", method = RequestMethod.POST)
  public Response deleteRelation(
      @RequestBody DeleteRelationRequest deleteRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response(
        deleteRelationBiz.process(
            deleteRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 更新关联标注 */
  @RequestMapping(value = "/update-relation", method = RequestMethod.POST)
  public Response updateRelation(
      @RequestBody UpdateRelationRequest updateRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response(
        updateRelationBiz.process(
            updateRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }
}
