package com.malgo.controller;

import com.malgo.biz.brat.exercise.CommitExerciseAnnotationBiz;
import com.malgo.biz.brat.exercise.DesignateUserExerciseBiz;
import com.malgo.biz.brat.exercise.ListContrastExerciseBiz;
import com.malgo.biz.brat.exercise.ListExerciseAnnotationBiz;
import com.malgo.biz.brat.exercise.ResetUserExerciseBiz;
import com.malgo.entity.UserAccount;
import com.malgo.request.DesignateAnnotationRequest;
import com.malgo.request.brat.CommitAnnotationRequest;
import com.malgo.request.exercise.ListExerciseAnnotationRequest;
import com.malgo.request.exercise.ListExerciseContrastRequest;
import com.malgo.request.exercise.UserResetRequest;
import com.malgo.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by cjl on 2018/6/3.
 */
@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationExerciseController extends BaseController {

  private final ListExerciseAnnotationBiz listExerciseAnnotationBiz;
  private final ResetUserExerciseBiz resetUserExerciseBiz;
  private final ListContrastExerciseBiz listContrastExerciseBiz;
  private final CommitExerciseAnnotationBiz commitExerciseAnnotationBiz;
  private final DesignateUserExerciseBiz designateUserExerciseBiz;

  public AnnotationExerciseController(ListExerciseAnnotationBiz listExerciseAnnotationBiz,
      ResetUserExerciseBiz resetUserExerciseBiz,
      ListContrastExerciseBiz listContrastExerciseBiz,
      CommitExerciseAnnotationBiz commitExerciseAnnotationBiz,
      DesignateUserExerciseBiz designateUserExerciseBiz) {
    this.listExerciseAnnotationBiz = listExerciseAnnotationBiz;
    this.resetUserExerciseBiz = resetUserExerciseBiz;
    this.listContrastExerciseBiz = listContrastExerciseBiz;
    this.commitExerciseAnnotationBiz = commitExerciseAnnotationBiz;
    this.designateUserExerciseBiz = designateUserExerciseBiz;
  }

  /**
   * 查询标注答案
   */
  @RequestMapping(value = "list-standard-exercise", method = RequestMethod.GET)
  public Response listStandardExercise(
      ListExerciseAnnotationRequest listExerciseAnnotationRequest) {
    return new Response(listExerciseAnnotationBiz.process(listExerciseAnnotationRequest, 0, 0));
  }

  /**
   * 用户习题重置
   */
  @RequestMapping(value = "user-exercise-reset", method = RequestMethod.POST)
  public Response userExerciseReset(@RequestBody UserResetRequest resetRequest) {
    return new Response(resetUserExerciseBiz.process(resetRequest, 0, 0));
  }

  /**
   * 习题集结果对照
   */
  @RequestMapping(value = "list-contrast-exercise", method = RequestMethod.GET)
  public Response listContrastExercise(ListExerciseContrastRequest listExerciseContrastRequest) {
    return new Response(listContrastExerciseBiz.process(listExerciseContrastRequest, 0, 0));
  }

  /**
   * 练习人员提交标注
   */
  @RequestMapping(value = "commit-user-exercise", method = RequestMethod.POST)
  public Response commitUserExercise(CommitAnnotationRequest commitAnnotationRequest,
      @ModelAttribute("userAccount") UserAccount userAccount) {
    return new Response(commitExerciseAnnotationBiz
        .process(commitAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /**
   * 练习题指派
   */
  @RequestMapping(value = "designate-exercise", method = RequestMethod.POST)
  public Response designateExercise(DesignateAnnotationRequest designateAnnotationRequest,
      @ModelAttribute("userAccount") UserAccount userAccount) {
    return new Response(designateUserExerciseBiz
        .process(designateAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }
}
