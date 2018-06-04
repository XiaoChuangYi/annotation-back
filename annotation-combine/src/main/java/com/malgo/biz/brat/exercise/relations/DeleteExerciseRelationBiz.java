package com.malgo.biz.brat.exercise.relations;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.UserExerciseRepository;
import com.malgo.entity.UserExercise;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.DeleteRelationRequest;
import com.malgo.service.RelationOperateService;
import com.malgo.utils.AnnotationConvert;
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
public class DeleteExerciseRelationBiz extends
    BaseBiz<DeleteRelationRequest, ExerciseAnnotationBratVO> {

  private final RelationOperateService exerciseRelationOperateService;
  private final UserExerciseRepository userExerciseRepository;

  public DeleteExerciseRelationBiz(
      @Qualifier("exercise-relation") RelationOperateService exerciseRelationOperateService,
      UserExerciseRepository userExerciseRepository) {
    this.exerciseRelationOperateService = exerciseRelationOperateService;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(DeleteRelationRequest deleteRelationRequest)
      throws InvalidInputException {
    if (deleteRelationRequest == null) {
      throw new InvalidInputException("valid-request", "无效的请求");
    }
    if (deleteRelationRequest.getId() <= 0) {
      throw new InvalidInputException("valid-id", "无效的id");
    }
    if (StringUtils.isBlank(deleteRelationRequest.getRTag())) {
      throw new InvalidInputException("valid-rTag", "参数rTag为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, DeleteRelationRequest deleteRelationRequest)
      throws BusinessRuleException {
    if (role > 2) {
      Optional<UserExercise> optional = userExerciseRepository
          .findById(deleteRelationRequest.getId());
      if (optional.isPresent()) {
        UserExercise userExercise = optional.get();
        if (userExercise.getAssignee() != userId) {
          throw new BusinessRuleException("no-permission-handle-current-record", "您无权操作当前记录！");
        }
      }
    }
  }

  @Override
  protected ExerciseAnnotationBratVO doBiz(DeleteRelationRequest deleteRelationRequest) {
    Optional<UserExercise> optional = userExerciseRepository
        .findById(deleteRelationRequest.getId());
    if (optional.isPresent()) {
      log.info("习题删除关系请求参数：{}",deleteRelationRequest);
      UserExercise userExercise = optional.get();
      userExercise.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      String annotation = exerciseRelationOperateService.deleteRelation(deleteRelationRequest);
      log.info("习题删除关系返回结果：{}",annotation);
      userExercise.setUserAnnotation(annotation);
      userExercise = userExerciseRepository.save(userExercise);
      return AnnotationConvert.convert2ExerciseAnnotationBratVO(userExercise);
    }
    return null;
  }
}
