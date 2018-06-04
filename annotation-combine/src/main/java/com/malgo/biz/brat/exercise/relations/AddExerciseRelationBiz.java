package com.malgo.biz.brat.exercise.relations;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.UserExerciseRepository;
import com.malgo.entity.UserExercise;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.AddRelationRequest;
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
public class AddExerciseRelationBiz extends BaseBiz<AddRelationRequest, ExerciseAnnotationBratVO> {

  private final RelationOperateService exerciseRelationOperateService;
  private final UserExerciseRepository userExerciseRepository;

  public AddExerciseRelationBiz(
      @Qualifier("exercise-relation") RelationOperateService exerciseRelationOperateService,
      UserExerciseRepository userExerciseRepository) {
    this.exerciseRelationOperateService = exerciseRelationOperateService;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(AddRelationRequest addRelationRequest)
      throws InvalidInputException {
    if (addRelationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (addRelationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(addRelationRequest.getRelation())) {
      throw new InvalidInputException("invalid-relation", "参数relation为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getSourceTag())) {
      throw new InvalidInputException("invalid-sourceTag", "参数sourceTag为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getTargetTag())) {
      throw new InvalidInputException("invalid-targetTag", "参数targetTag为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, AddRelationRequest addRelationRequest)
      throws BusinessRuleException {
    if (role > 2) {
      Optional<UserExercise> optional = userExerciseRepository.findById(addRelationRequest.getId());
      if (optional.isPresent()) {
        if (userId != optional.get().getAssignee()) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "当前人员无权操作当前记录!");
        }
      }
    }
  }

  @Override
  protected ExerciseAnnotationBratVO doBiz(AddRelationRequest addRelationRequest) {
    Optional<UserExercise> optional = userExerciseRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      log.info("习题新增关系请求参数：{}",addRelationRequest);
      UserExercise userExercise = optional.get();
      String annotation = exerciseRelationOperateService.addRelation(addRelationRequest);
      log.info("习题新增关系返回结果：{}",annotation);
      userExercise.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      userExercise.setUserAnnotation(annotation);
      userExercise = userExerciseRepository.save(userExercise);
      return AnnotationConvert.convert2ExerciseAnnotationBratVO(userExercise);
    }
    return null;
  }
}
