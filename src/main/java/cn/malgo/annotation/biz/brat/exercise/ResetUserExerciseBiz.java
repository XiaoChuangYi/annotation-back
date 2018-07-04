package cn.malgo.annotation.biz.brat.exercise;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.exercise.UserResetRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/4. */
@Component
public class ResetUserExerciseBiz extends BaseBiz<UserResetRequest, Object> {

  private final UserExerciseRepository userExerciseRepository;
  private final AnnotationCombineRepository annotationCombineRepository;

  public ResetUserExerciseBiz(
      UserExerciseRepository userExerciseRepository,
      AnnotationCombineRepository annotationCombineRepository) {
    this.userExerciseRepository = userExerciseRepository;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(UserResetRequest resetRequest) throws InvalidInputException {
    if (resetRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (resetRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-user-id", "无效的用户Id");
    }
  }

  @Override
  protected Object doBiz(UserResetRequest resetRequest) {
    List<UserExercise> userExerciseList =
        userExerciseRepository.findAllByAssigneeEquals(resetRequest.getUserId());
    List<Integer> annotationIdList =
        userExerciseList
            .stream()
            .map(userExercise -> userExercise.getAnnotationId())
            .collect(Collectors.toList());
    List<AnnotationCombine> annotationCombineList =
        annotationCombineRepository.findAllByIdInAndIsTaskEquals(annotationIdList, 1);
    Map<Integer, String> map = new HashMap<>();
    // 暂定练习模块的答案习题集预标注是字段final_annotation
    IntStream.range(0, annotationCombineList.size())
        .forEach(
            i -> {
              map.put(
                  annotationCombineList.get(i).getId(),
                  annotationCombineList.get(i).getFinalAnnotation());
            });
    userExerciseList
        .stream()
        .forEach(
            userExercise -> {
              userExercise.setUserAnnotation(map.get(userExercise.getAnnotationId()));
              userExercise.setState(AnnotationCombineStateEnum.preAnnotation.name());
            });
    userExerciseRepository.saveAll(userExerciseList);
    return null;
  }
}
