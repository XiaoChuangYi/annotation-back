package com.malgo.biz.brat.exercise;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dao.UserExerciseRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.entity.UserExercise;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.exercise.UserResetRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/4.
 */
@Component
public class ResetUserExerciseBiz extends BaseBiz<UserResetRequest, Object> {

  private final UserExerciseRepository userExerciseRepository;
  private final AnnotationCombineRepository annotationCombineRepository;

  public ResetUserExerciseBiz(UserExerciseRepository userExerciseRepository,
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
  protected void authorize(int userId, int role, UserResetRequest resetRequest)
      throws BusinessRuleException {

  }

  @Override
  protected Object doBiz(UserResetRequest resetRequest) {
    List<UserExercise> userExerciseList = userExerciseRepository
        .findAllByAssigneeEquals(resetRequest.getUserId());
    List<Integer> annotationIdList = userExerciseList.stream()
        .map(userExercise -> userExercise.getAnnotationId()).collect(
            Collectors.toList());
    List<AnnotationCombine> annotationCombineList = annotationCombineRepository
        .findAllByIdInAndIsTaskEquals(annotationIdList, 1);
    Map<Integer, String> map = new HashMap<>();
    //暂定练习模块的答案习题集预标注是字段final_annotation
    IntStream.range(0, annotationCombineList.size()).forEach(i -> {
      map.put(annotationCombineList.get(i).getId(),
          annotationCombineList.get(i).getFinalAnnotation());
    });
    userExerciseList.stream().forEach(userExercise -> {
          userExercise.setUserAnnotation(map.get(userExercise.getAnnotationId()));
          userExercise.setState(AnnotationCombineStateEnum.preAnnotation.name());
        }
    );
    userExerciseRepository.saveAll(userExerciseList);
    return null;
  }
}
