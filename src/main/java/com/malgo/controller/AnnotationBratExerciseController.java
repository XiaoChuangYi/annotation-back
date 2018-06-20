package com.malgo.controller;

import com.malgo.biz.brat.exercise.relations.AddExerciseRelationBiz;
import com.malgo.biz.brat.exercise.entities.AddUserExerciseBiz;
import com.malgo.biz.brat.exercise.relations.DeleteExerciseRelationBiz;
import com.malgo.biz.brat.exercise.entities.DeleteUserExerciseBiz;
import com.malgo.biz.brat.exercise.relations.UpdateExerciseRelationBiz;
import com.malgo.biz.brat.exercise.entities.UpdateUserExerciseBiz;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Created by cjl on 2018/6/4. */
@RestController
@Slf4j
@RequestMapping(value = "/api/v2/exercise")
public class AnnotationBratExerciseController extends BaseController {

  private final AddExerciseRelationBiz addExerciseRelationBiz;
  private final AddUserExerciseBiz addUserExerciseBiz;
  private final DeleteExerciseRelationBiz deleteExerciseRelationBiz;
  private final DeleteUserExerciseBiz deleteUserExerciseBiz;
  private final UpdateExerciseRelationBiz updateExerciseRelationBiz;
  private final UpdateUserExerciseBiz updateUserExerciseBiz;

  public AnnotationBratExerciseController(
      AddExerciseRelationBiz addExerciseRelationBiz,
      AddUserExerciseBiz addUserExerciseBiz,
      DeleteExerciseRelationBiz deleteExerciseRelationBiz,
      DeleteUserExerciseBiz deleteUserExerciseBiz,
      UpdateExerciseRelationBiz updateExerciseRelationBiz,
      UpdateUserExerciseBiz updateUserExerciseBiz) {
    this.addExerciseRelationBiz = addExerciseRelationBiz;
    this.addUserExerciseBiz = addUserExerciseBiz;
    this.deleteExerciseRelationBiz = deleteExerciseRelationBiz;
    this.deleteUserExerciseBiz = deleteUserExerciseBiz;
    this.updateExerciseRelationBiz = updateExerciseRelationBiz;
    this.updateUserExerciseBiz = updateUserExerciseBiz;
  }

  /** 新增标注 */
  @RequestMapping(value = "add-annotation", method = RequestMethod.POST)
  public Response addAnnotation(
      @RequestBody AddAnnotationRequest addAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        addUserExerciseBiz.process(
            addAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 删除标注 */
  @RequestMapping(value = "delete-annotation", method = RequestMethod.POST)
  public Response deleteAnnotation(
      @RequestBody DeleteAnnotationRequest deleteAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        deleteUserExerciseBiz.process(
            deleteAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 更新标注 */
  @RequestMapping(value = "update-annotation", method = RequestMethod.POST)
  public Response updateAnnotation(
      @RequestBody UpdateAnnotationRequest updateAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        updateUserExerciseBiz.process(
            updateAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 新增关系 */
  @RequestMapping(value = "add-relation", method = RequestMethod.POST)
  public Response addRelation(
      @RequestBody AddRelationRequest addRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        addExerciseRelationBiz.process(
            addRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 删除关系 */
  @RequestMapping(value = "delete-relation", method = RequestMethod.POST)
  public Response deleteRelation(
      @RequestBody DeleteRelationRequest deleteRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        deleteExerciseRelationBiz.process(
            deleteRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 更新标注 */
  @RequestMapping(value = "update-relation", method = RequestMethod.POST)
  public Response updateRelation(
      @RequestBody UpdateRelationRequest updateRelationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        updateExerciseRelationBiz.process(
            updateRelationRequest, userAccount.getId(), userAccount.getRoleId()));
  }
}
