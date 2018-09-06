package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.brat.ListRelationLimitRuleBiz;
import cn.malgo.annotation.biz.brat.task.GetAutoAnnotationBiz;
import cn.malgo.annotation.biz.brat.task.entities.AddAnnotationBiz;
import cn.malgo.annotation.biz.brat.task.entities.DeleteAnnotationBiz;
import cn.malgo.annotation.biz.brat.task.entities.UpdateAnnotationBiz;
import cn.malgo.annotation.biz.brat.task.relations.AddRelationBiz;
import cn.malgo.annotation.biz.brat.task.relations.DeleteRelationBiz;
import cn.malgo.annotation.biz.brat.task.relations.UpdateRelationBiz;
import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.request.brat.*;
import cn.malgo.annotation.vo.RelationLimitRuleVO;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.service.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationBratController {

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
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_DETAIL)
  @RequestMapping(value = "/get-auto-annotation", method = RequestMethod.GET)
  public Response getAutoAnnotation(@RequestParam("id") long id) {
    return new Response<>(getAutoAnnotationBiz.process(new GetAutoAnnotationRequest(id)));
  }

  /** 标注entities处理，新增标注的接口，不过算法api */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_INSERT)
  @RequestMapping(value = "/add-annotation", method = RequestMethod.POST)
  public Response addAnnotation(@RequestBody AddAnnotationGroupRequest request) {
    return new Response<>(addAnnotationBiz.process(request));
  }

  /** entities处理，更新标注 ，不过算法api */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_UPDATE)
  @RequestMapping(value = "/update-annotation", method = RequestMethod.POST)
  public Response updateAnnotation(@RequestBody UpdateAnnotationGroupRequest request) {
    return new Response<>(updateAnnotationBiz.process(request));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_DELETE)
  @RequestMapping(value = "/delete-annotation", method = RequestMethod.POST)
  public Response deleteAnnotation(@RequestBody DeleteAnnotationGroupRequest request) {
    return new Response<>(deleteAnnotationBiz.process(request));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_INSERT)
  @RequestMapping(value = "/add-relation", method = RequestMethod.POST)
  public Response addRelation(@RequestBody AddRelationRequest addRelationRequest) {
    return new Response<>(addRelationBiz.process(addRelationRequest));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_DELETE)
  @RequestMapping(value = "/delete-relation", method = RequestMethod.POST)
  public Response deleteRelation(@RequestBody DeleteRelationRequest deleteRelationRequest) {
    return new Response<>(deleteRelationBiz.process(deleteRelationRequest));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_UPDATE)
  @RequestMapping(value = "/update-relation", method = RequestMethod.POST)
  public Response updateRelation(@RequestBody UpdateRelationRequest updateRelationRequest) {
    return new Response<>(updateRelationBiz.process(updateRelationRequest));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_RELATION_LIMIT_RULE)
  @RequestMapping(value = "/list-relation-limit", method = RequestMethod.GET)
  public Response<RelationLimitRuleVO> listRelationLimit() {
    return new Response<>(listRelationLimitRuleBiz.process(null));
  }
}
