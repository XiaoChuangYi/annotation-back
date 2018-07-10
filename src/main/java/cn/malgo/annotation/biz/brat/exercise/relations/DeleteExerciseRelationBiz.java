package cn.malgo.annotation.biz.brat.exercise.relations;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.DeleteRelationRequest;
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
public class DeleteExerciseRelationBiz
    extends BaseBiz<DeleteRelationRequest, ExerciseAnnotationBratVO> {

  private final RelationOperateService exerciseRelationOperateService;
  private final UserExerciseRepository userExerciseRepository;
  private int globalRole;
  private int globalUserId;

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
    if (StringUtils.isBlank(deleteRelationRequest.getReTag())) {
      throw new InvalidInputException("valid-rTag", "参数rTag为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, DeleteRelationRequest deleteRelationRequest)
      throws BusinessRuleException {
    globalRole = role;
    globalUserId = userId;
    if (role > 2) {
      Optional<UserExercise> optional =
          userExerciseRepository.findById(deleteRelationRequest.getId());
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
    Optional<UserExercise> optional =
        userExerciseRepository.findById(deleteRelationRequest.getId());
    if (optional.isPresent()) {
      log.info("习题删除关系请求参数：{}", deleteRelationRequest);
      UserExercise userExercise = optional.get();
      userExercise.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      String annotation = exerciseRelationOperateService.deleteRelation(deleteRelationRequest, 0);
      log.info("习题删除关系返回结果：{}", annotation);
      userExercise.setUserAnnotation(annotation);
      userExercise = userExerciseRepository.save(userExercise);
      ExerciseAnnotationBratVO exerciseAnnotationBratVO =
          AnnotationConvert.convert2ExerciseAnnotationBratVO(userExercise);
      return exerciseAnnotationBratVO;
    }
    return null;
  }
}
