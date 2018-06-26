package cn.malgo.annotation.biz.brat.exercise.entities;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
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
public class AddUserExerciseBiz extends BaseBiz<AddAnnotationRequest, ExerciseAnnotationBratVO> {

  private final AnnotationOperateService exerciseAnnotationOperateService;
  private final UserExerciseRepository userExerciseRepository;
  private int globalRole;
  private int globalUserId;

  public AddUserExerciseBiz(
      @Qualifier("exercise-entity") AnnotationOperateService exerciseAnnotationOperateService,
      UserExerciseRepository userExerciseRepository) {
    this.exerciseAnnotationOperateService = exerciseAnnotationOperateService;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(AddAnnotationRequest addAnnotationRequest)
      throws InvalidInputException {
    if (addAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (addAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(addAnnotationRequest.getTerm())) {
      throw new InvalidInputException("invalid-term", "term参数为空");
    }
    if (StringUtils.isBlank(addAnnotationRequest.getType())) {
      throw new InvalidInputException("invalid-annotation-type", "annotationType参数为空");
    }
    if (addAnnotationRequest.getStartPosition() < 0) {
      throw new InvalidInputException("invalid-start-position", "无效的startPosition");
    }
    if (addAnnotationRequest.getEndPosition() <= 0) {
      throw new InvalidInputException("invalid-end-position", "无效的endPosition");
    }
  }

  @Override
  protected void authorize(int userId, int role, AddAnnotationRequest addAnnotationRequest)
      throws BusinessRuleException {
    globalRole = role;
    globalUserId = userId;
    if (role > 2) { // 标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<UserExercise> optional =
          userExerciseRepository.findById(addAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "当前人员无权操作当前记录");
        }
      }
    }
  }

  @Override
  protected ExerciseAnnotationBratVO doBiz(AddAnnotationRequest addAnnotationRequest) {
    // 练习人员
    Optional<UserExercise> optional = userExerciseRepository.findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      log.info("习题新增标注请求参数：{}", addAnnotationRequest);
      UserExercise userExercise = optional.get();
      String annotation =
          exerciseAnnotationOperateService.addAnnotation(addAnnotationRequest, globalRole);
      log.info("习题新增标注返回结果：{}", annotation);
      userExercise.setUserAnnotation(annotation);
      userExercise.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      userExercise = userExerciseRepository.save(userExercise);
      ExerciseAnnotationBratVO exerciseAnnotationBratVO =
          AnnotationConvert.convert2ExerciseAnnotationBratVO(userExercise);
      return exerciseAnnotationBratVO;
    }
    return null;
  }
}
