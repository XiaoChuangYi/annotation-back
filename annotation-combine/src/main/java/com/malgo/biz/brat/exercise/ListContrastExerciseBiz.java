package com.malgo.biz.brat.exercise;

import com.alibaba.fastjson.JSONObject;
import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dao.UserExerciseRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.entity.UserExercise;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.exercise.ListExerciseContrastRequest;
import com.malgo.result.PageVO;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.ExerciseAnnotationContrastBratVO;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/4.
 */
@Component
public class ListContrastExerciseBiz extends
    BaseBiz<ListExerciseContrastRequest, PageVO<ExerciseAnnotationContrastBratVO>> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final UserExerciseRepository userExerciseRepository;

  public ListContrastExerciseBiz(AnnotationCombineRepository annotationCombineRepository,
      UserExerciseRepository userExerciseRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(ListExerciseContrastRequest listExerciseContrastRequest)
      throws InvalidInputException {
    if (listExerciseContrastRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (listExerciseContrastRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-userId", "无效的userId");
    }
  }

  @Override
  protected void authorize(int userId, int role,
      ListExerciseContrastRequest listExerciseContrastRequest)
      throws BusinessRuleException {
  }

  @Override
  protected PageVO<ExerciseAnnotationContrastBratVO> doBiz(
      ListExerciseContrastRequest listExerciseContrastRequest) {
    Page<UserExercise> page = userExerciseRepository
        .findAllByAssigneeEquals(listExerciseContrastRequest.getUserId(),
            PageRequest.of(listExerciseContrastRequest.getPageIndex(),
                listExerciseContrastRequest.getPageSize()));
    List<UserExercise> userExerciseList = page.getContent();
    List<Integer> anIdList = userExerciseList.stream()
        .map(userExercise -> userExercise.getAnnotationId())
        .collect(Collectors.toList());
    List<AnnotationCombine> annotationCombineList = annotationCombineRepository
        .findAllByIdInAndIsTaskEquals(anIdList, 1);
    Map<Integer, String> map = new HashMap<>();
    //提取出标准答案
    IntStream.range(0, annotationCombineList.size()).forEach(i ->
        map.put(annotationCombineList.get(i).getId(),
            annotationCombineList.get(i).getReviewedAnnotation())
    );
    List<ExerciseAnnotationContrastBratVO> exerciseAnnotationContrastBratVOList = new LinkedList<>();
    for (UserExercise current : userExerciseList) {
      ExerciseAnnotationContrastBratVO exerciseAnnotationContrastBratVO = new ExerciseAnnotationContrastBratVO();
      BeanUtils.copyProperties(current, exerciseAnnotationContrastBratVO);
      JSONObject userJson = AnnotationConvert
          .convertAnnotation2BratFormat(current.getTerm(), current.getUserAnnotation());
      exerciseAnnotationContrastBratVO.setUserAnnotation(userJson);

      JSONObject standardJson = AnnotationConvert
          .convertAnnotation2BratFormat(current.getTerm(), map.get(current.getAnnotationId()));
      exerciseAnnotationContrastBratVO.setStandardAnnotation(standardJson);

      boolean result = AnnotationConvert
          .compareAnnotation(current.getUserAnnotation(), map.get(current.getAnnotationId()));
      exerciseAnnotationContrastBratVO.setResult(result);
      exerciseAnnotationContrastBratVOList.add(exerciseAnnotationContrastBratVO);
    }
    PageVO<ExerciseAnnotationContrastBratVO> pageVO = new PageVO<>();
    pageVO.setTotal(page.getTotalElements());
    pageVO.setDataList(exerciseAnnotationContrastBratVOList);
    return pageVO;
  }
}
