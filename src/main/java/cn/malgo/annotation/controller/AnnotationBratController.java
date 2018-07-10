package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.brat.ListRelationLimitRuleBiz;
import cn.malgo.annotation.biz.brat.task.GetAutoAnnotationBiz;
import cn.malgo.annotation.biz.brat.task.entities.AddAnnotationBiz;
import cn.malgo.annotation.biz.brat.task.entities.DeleteAnnotationBiz;
import cn.malgo.annotation.biz.brat.task.entities.UpdateAnnotationBiz;
import cn.malgo.annotation.biz.brat.task.relations.AddRelationBiz;
import cn.malgo.annotation.biz.brat.task.relations.DeleteRelationBiz;
import cn.malgo.annotation.biz.brat.task.relations.UpdateRelationBiz;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.DeleteAnnotationRequest;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.result.Response;
import cn.malgo.annotation.vo.RelationLimitRuleVO;
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
  private final ListRelationLimitRuleBiz listRelationLimitRuleBiz;

  public AnnotationBratController(
      GetAutoAnnotationBiz getAutoAnnotationBiz,
      AddAnnotationBiz addAnnotationBiz,
      UpdateAnnotationBiz updateAnnotationBiz,
      DeleteAnnotationBiz deleteAnnotationBiz,
      AddRelationBiz addRelationBiz,
      UpdateRelationBiz updateRelationBiz,
      DeleteRelationBiz deleteRelationBiz,
      ListRelationLimitRuleBiz listRelationLimitRuleBiz) {
    this.getAutoAnnotationBiz = getAutoAnnotationBiz;
    this.addAnnotationBiz = addAnnotationBiz;
    this.updateAnnotationBiz = updateAnnotationBiz;
    this.deleteAnnotationBiz = deleteAnnotationBiz;
    this.addRelationBiz = addRelationBiz;
    this.updateRelationBiz = updateRelationBiz;
    this.deleteRelationBiz = deleteRelationBiz;
    this.listRelationLimitRuleBiz = listRelationLimitRuleBiz;
  }

  /** 获取算法服务的预标注结果 */
  @RequestMapping(value = "/get-auto-annotation", method = RequestMethod.GET)
  public Response getAutoAnnotation(
      @RequestParam("id") int id,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        getAutoAnnotationBiz.process(
            new GetAutoAnnotationRequest(id), userAccount.getId(), userAccount.getRoleId()));
  }

  /** 标注entities处理，新增标注的接口，不过算法api */
  @RequestMapping(value = "/add-annotation", method = RequestMethod.POST)
  public Response addAnnotation(
      @RequestBody AddAnnotationRequest addAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        addAnnotationBiz.process(
            addAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** entities处理，更新标注 ，不过算法api */
  @RequestMapping(value = "/update-annotation", method = RequestMethod.POST)
  public Response updateAnnotation(
      @RequestBody UpdateAnnotationRequest updateAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        updateAnnotationBiz.process(
            updateAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** entities处理，删除标注，不过算法api */
  @RequestMapping(value = "/delete-annotation", method = RequestMethod.POST)
  public Response deleteAnnotation(
      @RequestBody DeleteAnnotationRequest deleteAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        deleteAnnotationBiz.process(
            deleteAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 普通人员，新增关联标注 */
  @RequestMapping(value = "add-relation", method = RequestMethod.POST)
  public Response addRelation(
      @RequestBody AddRelationRequest addRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        addRelationBiz.process(addRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 审核人员 删除关联标注 */
  @RequestMapping(value = "/delete-relation", method = RequestMethod.POST)
  public Response deleteRelation(
      @RequestBody DeleteRelationRequest deleteRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        deleteRelationBiz.process(
            deleteRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 更新关联标注 */
  @RequestMapping(value = "/update-relation", method = RequestMethod.POST)
  public Response updateRelation(
      @RequestBody UpdateRelationRequest updateRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        updateRelationBiz.process(
            updateRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 关联标注限制规则列表 */
  @RequestMapping(value = "/list-relation-limit", method = RequestMethod.GET)
  public Response<RelationLimitRuleVO> listRelationLimit(
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(listRelationLimitRuleBiz.process(null, userAccount));
  }
}
