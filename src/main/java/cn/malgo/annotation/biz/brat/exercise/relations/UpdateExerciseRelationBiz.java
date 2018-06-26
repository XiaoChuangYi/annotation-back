package cn.malgo.annotation.biz.brat.exercise.relations;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.ExerciseAnnotationBratVO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/4. */
@Component
@Slf4j
public class UpdateExerciseRelationBiz
    extends BaseBiz<UpdateRelationRequest, ExerciseAnnotationBratVO> {

  private final RelationOperateService exerciseRelationOperateService;
  private final UserExerciseRepository userExerciseRepository;

  public UpdateExerciseRelationBiz(
      @Qualifier("exercise-relation") RelationOperateService exerciseRelationOperateService,
      UserExerciseRepository userExerciseRepository) {
    this.exerciseRelationOperateService = exerciseRelationOperateService;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(UpdateRelationRequest updateRelationRequest)
      throws InvalidInputException {
    if (updateRelationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (updateRelationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(updateRelationRequest.getReTag())) {
      throw new InvalidInputException("invalid-rTag", "参数rTag为空");
    }
    if (StringUtils.isBlank(updateRelationRequest.getRelation())) {
      throw new InvalidInputException("invalid-relation", "参数relation为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, UpdateRelationRequest updateRelationRequest)
      throws BusinessRuleException {
    if (role > 2) {
      Optional<UserExercise> optional =
          userExerciseRepository.findById(updateRelationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "您无权操作该条记录");
        }
      }
    }
  }

  @Override
  protected ExerciseAnnotationBratVO doBiz(UpdateRelationRequest updateRelationRequest) {
    Optional<UserExercise> optional =
        userExerciseRepository.findById(updateRelationRequest.getId());
    if (optional.isPresent()) {
      log.info("习题更新关系请求参数：{}", updateRelationRequest);
      UserExercise userExercise = optional.get();
      userExercise.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      String annotation = exerciseRelationOperateService.updateRelation(updateRelationRequest);
      log.info("习题更新关系返回结果：{}", annotation);
      userExercise.setUserAnnotation(annotation);
      userExercise = userExerciseRepository.save(userExercise);
      return AnnotationConvert.convert2ExerciseAnnotationBratVO(userExercise);
    }
    return null;
  }
}
