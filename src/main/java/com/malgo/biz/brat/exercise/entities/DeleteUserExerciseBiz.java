package com.malgo.biz.brat.exercise.entities;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.UserExerciseRepository;
import com.malgo.entity.UserExercise;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.utils.OpLoggerUtil;
import com.malgo.vo.ExerciseAnnotationBratVO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/4.
 */
@Component
@Slf4j
public class DeleteUserExerciseBiz extends
    BaseBiz<DeleteAnnotationRequest, ExerciseAnnotationBratVO> {

  private final AnnotationOperateService exerciseAnnotationOperateService;
  private final UserExerciseRepository userExerciseRepository;
  private int globalRole;
  private int globalUserId;

  public DeleteUserExerciseBiz(
      @Qualifier("exercise-entity") AnnotationOperateService exerciseAnnotationOperateService,
      UserExerciseRepository userExerciseRepository) {
    this.exerciseAnnotationOperateService = exerciseAnnotationOperateService;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(DeleteAnnotationRequest deleteAnnotationRequest)
      throws InvalidInputException {
    if (deleteAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (deleteAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(deleteAnnotationRequest.getTag())) {
      throw new InvalidInputException("invalid-tag", "参数tag为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, DeleteAnnotationRequest deleteAnnotationRequest)
      throws BusinessRuleException {
    globalRole = role;
    globalUserId = userId;
    if (role > 2) {//标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<UserExercise> optional = userExerciseRepository
          .findById(deleteAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (userId != optional.get().getAssignee()) {
          throw new BusinessRuleException("no-authorize-current-record", "当前练习人员无权操作该条记录");
        }
      }
    }
  }

  @Override
  protected ExerciseAnnotationBratVO doBiz(DeleteAnnotationRequest deleteAnnotationRequest) {
    Optional<UserExercise> optional = userExerciseRepository
        .findById(deleteAnnotationRequest.getId());
    if (optional.isPresent()) {
      log.info("习题删除标注请求参数：{}", deleteAnnotationRequest);
      UserExercise userExercise = new UserExercise();
      String annotation = exerciseAnnotationOperateService
          .deleteAnnotation(deleteAnnotationRequest);
      log.info("习题删除标注返回结果：{}", annotation);
      userExercise.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      userExercise.setUserAnnotation(annotation);
      userExercise = userExerciseRepository.save(userExercise);
      ExerciseAnnotationBratVO exerciseAnnotationBratVO = AnnotationConvert
          .convert2ExerciseAnnotationBratVO(userExercise);
      OpLoggerUtil.info(globalUserId, globalRole, "delete-exercise-annotation", "success");
      return exerciseAnnotationBratVO;
    }
    OpLoggerUtil.info(globalUserId, globalRole, "delete-exercise-annotation", "无对应id记录");
    return null;
  }
}