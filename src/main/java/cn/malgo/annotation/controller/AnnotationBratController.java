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
import cn.malgo.annotation.service.UserCenterService;
import cn.malgo.annotation.vo.RelationLimitRuleVO;
import cn.malgo.common.auth.PermissionAnno;
import cn.malgo.common.auth.user.UserDetailService;
import cn.malgo.service.model.Response;
import cn.malgo.service.model.UserDetails;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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
  private final UserCenterService userCenterService;
  private final UserDetailService userDetailService;

  public AnnotationBratController(
      GetAutoAnnotationBiz getAutoAnnotationBiz,
      AddAnnotationBiz addAnnotationBiz,
      UpdateAnnotationBiz updateAnnotationBiz,
      DeleteAnnotationBiz deleteAnnotationBiz,
      AddRelationBiz addRelationBiz,
      UpdateRelationBiz updateRelationBiz,
      DeleteRelationBiz deleteRelationBiz,
      ListRelationLimitRuleBiz listRelationLimitRuleBiz,
      final UserCenterService userCenterService,
      final UserDetailService userDetailService) {
    this.getAutoAnnotationBiz = getAutoAnnotationBiz;
    this.addAnnotationBiz = addAnnotationBiz;
    this.updateAnnotationBiz = updateAnnotationBiz;
    this.deleteAnnotationBiz = deleteAnnotationBiz;
    this.addRelationBiz = addRelationBiz;
    this.updateRelationBiz = updateRelationBiz;
    this.deleteRelationBiz = deleteRelationBiz;
    this.listRelationLimitRuleBiz = listRelationLimitRuleBiz;
    this.userCenterService = userCenterService;
    this.userDetailService = userDetailService;
  }

  /** 用户列表查询 */
  @PermissionAnno(PermissionConstant.ANNOTATION_USER_LIST)
  @RequestMapping(value = "/list-user-account", method = RequestMethod.GET)
  public Response listUserAccount() {
    return new Response<>(userCenterService.getUsersByUserCenter());
  }

  /** 获取算法服务的预标注结果 */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_DETAIL)
  @RequestMapping(value = "/get-auto-annotation", method = RequestMethod.GET)
  public Response getAutoAnnotation(
      @RequestParam("id") long id, HttpServletRequest servletRequest) {
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
    return new Response<>(
        getAutoAnnotationBiz.process(new GetAutoAnnotationRequest(id), userDetails));
  }

  /** 标注entities处理，新增标注的接口，不过算法api */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_INSERT)
  @RequestMapping(value = "/add-annotation", method = RequestMethod.POST)
  public Response addAnnotation(
      @RequestBody AddAnnotationGroupRequest request, HttpServletRequest servletRequest) {
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
    return new Response<>(addAnnotationBiz.process(request, userDetails));
  }

  /** entities处理，更新标注 ，不过算法api */
  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_UPDATE)
  @RequestMapping(value = "/update-annotation", method = RequestMethod.POST)
  public Response updateAnnotation(
      @RequestBody UpdateAnnotationGroupRequest request, HttpServletRequest servletRequest) {
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
    return new Response<>(updateAnnotationBiz.process(request, userDetails));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_DELETE)
  @RequestMapping(value = "/delete-annotation", method = RequestMethod.POST)
  public Response deleteAnnotation(
      @RequestBody DeleteAnnotationGroupRequest request, HttpServletRequest servletRequest) {
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
    return new Response<>(deleteAnnotationBiz.process(request, userDetails));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_INSERT)
  @RequestMapping(value = "/add-relation", method = RequestMethod.POST)
  public Response addRelation(
      @RequestBody AddRelationRequest addRelationRequest, HttpServletRequest servletRequest) {
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
    return new Response<>(addRelationBiz.process(addRelationRequest, userDetails));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_DELETE)
  @RequestMapping(value = "/delete-relation", method = RequestMethod.POST)
  public Response deleteRelation(
      @RequestBody DeleteRelationRequest deleteRelationRequest, HttpServletRequest servletRequest) {
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
    return new Response<>(deleteRelationBiz.process(deleteRelationRequest, userDetails));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_TASK_UPDATE)
  @RequestMapping(value = "/update-relation", method = RequestMethod.POST)
  public Response updateRelation(
      @RequestBody UpdateRelationRequest updateRelationRequest, HttpServletRequest servletRequest) {
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
    return new Response<>(updateRelationBiz.process(updateRelationRequest, userDetails));
  }

  @PermissionAnno(PermissionConstant.ANNOTATION_RELATION_LIMIT_RULE)
  @RequestMapping(value = "/list-relation-limit", method = RequestMethod.GET)
  public Response<RelationLimitRuleVO> listRelationLimit() {
    return new Response<>(listRelationLimitRuleBiz.process(null, null));
  }
}
