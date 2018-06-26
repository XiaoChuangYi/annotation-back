package cn.malgo.annotation.biz.brat.exercise;

import com.alibaba.fastjson.JSONObject;
import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.exercise.ListExerciseContrastRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.ExerciseAnnotationContrastBratVO;
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

/** Created by cjl on 2018/6/4. */
@Component
public class ListContrastExerciseBiz
    extends BaseBiz<ListExerciseContrastRequest, PageVO<ExerciseAnnotationContrastBratVO>> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final UserExerciseRepository userExerciseRepository;

  public ListContrastExerciseBiz(
      AnnotationCombineRepository annotationCombineRepository,
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
    if (listExerciseContrastRequest.getPageIndex() < 1) {
      throw new InvalidInputException("invalid-page-index", "pageIndex应该大于等于1");
    }
    if (listExerciseContrastRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "pageSize应该大于等于0");
    }
    if (listExerciseContrastRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-user-id", "无效的userId");
    }
  }

  @Override
  protected void authorize(
      int userId, int role, ListExerciseContrastRequest listExerciseContrastRequest)
      throws BusinessRuleException {}

  @Override
  protected PageVO<ExerciseAnnotationContrastBratVO> doBiz(
      ListExerciseContrastRequest listExerciseContrastRequest) {

    listExerciseContrastRequest.setPageIndex(listExerciseContrastRequest.getPageIndex() - 1);

    Page<UserExercise> page =
        userExerciseRepository.findUserExercisesByAssigneeEquals(
            listExerciseContrastRequest.getUserId(),
            PageRequest.of(
                listExerciseContrastRequest.getPageIndex(),
                listExerciseContrastRequest.getPageSize()));

    List<UserExercise> userExerciseList = page.getContent();
    List<Integer> anIdList =
        userExerciseList
            .stream()
            .map(userExercise -> userExercise.getAnnotationId())
            .collect(Collectors.toList());

    List<AnnotationCombine> annotationCombineList =
        annotationCombineRepository.findAllByIdInAndIsTaskEquals(anIdList, 1);
    Map<Integer, String> map = new HashMap<>();
    // 提取出标准答案
    IntStream.range(0, annotationCombineList.size())
        .forEach(
            i ->
                map.put(
                    annotationCombineList.get(i).getId(),
                    annotationCombineList.get(i).getReviewedAnnotation()));

    List<ExerciseAnnotationContrastBratVO> exerciseAnnotationContrastBratVOList =
        new LinkedList<>();
    for (UserExercise current : userExerciseList) {
      ExerciseAnnotationContrastBratVO exerciseAnnotationContrastBratVO =
          new ExerciseAnnotationContrastBratVO();
      BeanUtils.copyProperties(current, exerciseAnnotationContrastBratVO);
      JSONObject userJson =
          AnnotationConvert.convertAnnotation2BratFormat(
              current.getTerm(), current.getUserAnnotation(), current.getAnnotationType());
      exerciseAnnotationContrastBratVO.setUserAnnotation(userJson);

      JSONObject standardJson =
          AnnotationConvert.convertAnnotation2BratFormat(
              current.getTerm(), map.get(current.getAnnotationId()), current.getAnnotationType());
      exerciseAnnotationContrastBratVO.setStandardAnnotation(standardJson);

      boolean result =
          AnnotationConvert.compareAnnotation(
              current.getUserAnnotation(), map.get(current.getAnnotationId()));
      exerciseAnnotationContrastBratVO.setResult(result);
      exerciseAnnotationContrastBratVOList.add(exerciseAnnotationContrastBratVO);
    }
    PageVO<ExerciseAnnotationContrastBratVO> pageVO = new PageVO<>();
    pageVO.setTotal(page.getTotalElements());
    pageVO.setDataList(exerciseAnnotationContrastBratVOList);
    return pageVO;
  }
}
