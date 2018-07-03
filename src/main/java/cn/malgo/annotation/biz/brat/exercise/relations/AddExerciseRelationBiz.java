package cn.malgo.annotation.biz.brat.exercise.relations;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.service.RelationOperateService;
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
public class AddExerciseRelationBiz extends BaseBiz<AddRelationRequest, ExerciseAnnotationBratVO> {

  private final RelationOperateService exerciseRelationOperateService;
  private final UserExerciseRepository userExerciseRepository;
  private int globalRole;
  private int globalUserId;

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
      throw new InvalidInputException("invalid-source-tag", "参数sourceTag为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getTargetTag())) {
      throw new InvalidInputException("invalid-target-tag", "参数targetTag为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, AddRelationRequest addRelationRequest)
      throws BusinessRuleException {
    globalRole = role;
    globalUserId = userId;
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
      log.info("习题新增关系请求参数：{}", addRelationRequest);
      UserExercise userExercise = optional.get();
      String annotation = exerciseRelationOperateService.addRelation(addRelationRequest, 0);
      log.info("习题新增关系返回结果：{}", annotation);
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
