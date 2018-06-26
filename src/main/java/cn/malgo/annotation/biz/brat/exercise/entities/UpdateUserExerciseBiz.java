package cn.malgo.annotation.biz.brat.exercise.entities;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.utils.OpLoggerUtil;
import cn.malgo.annotation.vo.ExerciseAnnotationBratVO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/4. */
@Component
@Slf4j
public class UpdateUserExerciseBiz
    extends BaseBiz<UpdateAnnotationRequest, ExerciseAnnotationBratVO> {

  private final AnnotationOperateService exerciseAnnotationOperateService;
  private final UserExerciseRepository userExerciseRepository;
  private int globalRole;
  private int globalUserId;

  public UpdateUserExerciseBiz(
      @Qualifier("exercise-entity") AnnotationOperateService exerciseAnnotationOperateService,
      UserExerciseRepository userExerciseRepository) {
    this.exerciseAnnotationOperateService = exerciseAnnotationOperateService;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(UpdateAnnotationRequest updateAnnotationRequest)
      throws InvalidInputException {
    if (updateAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (updateAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(updateAnnotationRequest.getTag())) {
      throw new InvalidInputException("invalid-tag", "参数tag不能为空");
    }
    if (StringUtils.isBlank(updateAnnotationRequest.getNewType())) {
      throw new InvalidInputException("invalid-newType", "参数newType不能为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, UpdateAnnotationRequest updateAnnotationRequest)
      throws BusinessRuleException {
    globalRole = role;
    globalUserId = userId;
    if (role > 2) { // 标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<UserExercise> optional =
          userExerciseRepository.findById(updateAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-current-record", "当前练习人员无权操作当前记录");
        }
      }
    }
  }

  @Override
  protected ExerciseAnnotationBratVO doBiz(UpdateAnnotationRequest updateAnnotationRequest) {
    Optional<UserExercise> optional =
        userExerciseRepository.findById(updateAnnotationRequest.getId());
    if (optional.isPresent()) {
      log.info("习题更新标注请求参数：{}", updateAnnotationRequest);
      UserExercise userExercise = optional.get();
      String annotation =
          exerciseAnnotationOperateService.updateAnnotation(updateAnnotationRequest);
      log.info("习题更新标注返回结果：{}", annotation);
      userExercise.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      userExercise.setUserAnnotation(annotation);
      userExercise = userExerciseRepository.save(userExercise);
      ExerciseAnnotationBratVO exerciseAnnotationBratVO =
          AnnotationConvert.convert2ExerciseAnnotationBratVO(userExercise);
      return exerciseAnnotationBratVO;
    }
    return null;
  }
}
