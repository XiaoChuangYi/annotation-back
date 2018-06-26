package cn.malgo.annotation.controller;

import cn.malgo.annotation.biz.brat.exercise.CommitExerciseAnnotationBiz;
import cn.malgo.annotation.biz.brat.exercise.DesignateUserExerciseBiz;
import cn.malgo.annotation.biz.brat.exercise.ListContrastExerciseBiz;
import cn.malgo.annotation.biz.brat.exercise.ListExerciseAnnotationBiz;
import cn.malgo.annotation.biz.brat.exercise.ResetUserExerciseBiz;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.annotation.request.exercise.ListExerciseAnnotationRequest;
import cn.malgo.annotation.request.exercise.ListExerciseContrastRequest;
import cn.malgo.annotation.request.exercise.UserResetRequest;
import cn.malgo.annotation.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Created by cjl on 2018/6/3. */
@RestController
@RequestMapping(value = "/api/v2")
@Slf4j
public class AnnotationExerciseController extends BaseController {

  private final ListExerciseAnnotationBiz listExerciseAnnotationBiz;
  private final ResetUserExerciseBiz resetUserExerciseBiz;
  private final ListContrastExerciseBiz listContrastExerciseBiz;
  private final CommitExerciseAnnotationBiz commitExerciseAnnotationBiz;
  private final DesignateUserExerciseBiz designateUserExerciseBiz;

  public AnnotationExerciseController(
      ListExerciseAnnotationBiz listExerciseAnnotationBiz,
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

  /** 查询标注答案 */
  @RequestMapping(value = "list-standard-exercise", method = RequestMethod.GET)
  public Response listStandardExercise(
      ListExerciseAnnotationRequest listExerciseAnnotationRequest) {
    return new Response<>(listExerciseAnnotationBiz.process(listExerciseAnnotationRequest, 0, 0));
  }

  /** 用户习题重置 */
  @RequestMapping(value = "user-exercise-reset", method = RequestMethod.POST)
  public Response userExerciseReset(@RequestBody UserResetRequest resetRequest) {
    return new Response<>(resetUserExerciseBiz.process(resetRequest, 0, 0));
  }

  /** 习题集结果对照 */
  @RequestMapping(value = "list-contrast-exercise", method = RequestMethod.GET)
  public Response listContrastExercise(ListExerciseContrastRequest listExerciseContrastRequest) {
    return new Response<>(listContrastExerciseBiz.process(listExerciseContrastRequest, 0, 0));
  }

  /** 练习人员提交标注 */
  @RequestMapping(value = "commit-user-exercise", method = RequestMethod.POST)
  public Response commitUserExercise(
      @RequestBody CommitAnnotationRequest commitAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        commitExerciseAnnotationBiz.process(
            commitAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }

  /** 练习题指派 */
  @RequestMapping(value = "designate-exercise", method = RequestMethod.POST)
  public Response designateExercise(
      @RequestBody DesignateAnnotationRequest designateAnnotationRequest,
      @ModelAttribute(value = "userAccount", binding = false) UserAccount userAccount) {
    return new Response<>(
        designateUserExerciseBiz.process(
            designateAnnotationRequest, userAccount.getId(), userAccount.getRoleId()));
  }
}
