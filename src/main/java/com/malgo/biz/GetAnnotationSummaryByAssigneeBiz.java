package com.malgo.biz;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dao.UserExerciseRepository;
import com.malgo.dto.AnnotationSummary;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.SetUserStateRequest;
import com.malgo.vo.AnnotationSummaryVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/5. */
@Component
public class GetAnnotationSummaryByAssigneeBiz
    extends BaseBiz<SetUserStateRequest, List<AnnotationSummaryVO>> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final UserExerciseRepository userExerciseRepository;

  public GetAnnotationSummaryByAssigneeBiz(
      AnnotationCombineRepository annotationCombineRepository,
      UserExerciseRepository userExerciseRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(SetUserStateRequest setUserStateRequest)
      throws InvalidInputException {
    if (setUserStateRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (setUserStateRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-user-id", "无效的用户id");
    }
  }

  @Override
  protected void authorize(int userId, int role, SetUserStateRequest setUserStateRequest)
      throws BusinessRuleException {}

  @Override
  protected List<AnnotationSummaryVO> doBiz(
      int userId, int role, SetUserStateRequest setUserStateRequest) {
    List<AnnotationSummaryVO> finalAnnotationSummaryVOList = null;
    List<AnnotationSummary> annotationSummaryList;
    if (role == 3) { // 标注人员
      annotationSummaryList =
          annotationCombineRepository.findByAssigneeAndStateGroup(setUserStateRequest.getUserId());
      finalAnnotationSummaryVOList =
          annotationSummaryList
              .stream()
              .map(x -> new AnnotationSummaryVO(x.getState(), x.getNum()))
              .collect(Collectors.toList());
    }
    if (role == 4) { // 练习人员
      annotationSummaryList =
          userExerciseRepository.findByAssigneeAndStateGroup(setUserStateRequest.getUserId());
      finalAnnotationSummaryVOList =
          annotationSummaryList
              .stream()
              .map(x -> new AnnotationSummaryVO(x.getState(), x.getNum()))
              .collect(Collectors.toList());
    }
    return finalAnnotationSummaryVOList;
  }
}
