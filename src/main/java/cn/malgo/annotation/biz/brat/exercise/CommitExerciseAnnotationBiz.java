package cn.malgo.annotation.biz.brat.exercise;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/4. */
@Component
public class CommitExerciseAnnotationBiz extends BaseBiz<CommitAnnotationRequest, Object> {

  private final UserExerciseRepository userExerciseRepository;

  public CommitExerciseAnnotationBiz(UserExerciseRepository userExerciseRepository) {
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(CommitAnnotationRequest commitAnnotationRequest)
      throws InvalidInputException {
    if (commitAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (commitAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的用户id");
    }
  }

  @Override
  protected void authorize(int userId, int role, CommitAnnotationRequest commitAnnotationRequest)
      throws BusinessRuleException {
    if (role > 2) {
      Optional<UserExercise> optional =
          userExerciseRepository.findById(commitAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-permission-handle-record", "您无权操作该条记录");
        }
      }
    }
  }

  @Override
  protected Object doBiz(CommitAnnotationRequest commitAnnotationRequest) {
    Optional<UserExercise> optional =
        userExerciseRepository.findById(commitAnnotationRequest.getId());
    if (optional.isPresent()) {
      UserExercise userExercise = optional.get();
      userExercise.setState(AnnotationCombineStateEnum.preExamine.name());
      return userExerciseRepository.save(userExercise);
    }
    return null;
  }
}