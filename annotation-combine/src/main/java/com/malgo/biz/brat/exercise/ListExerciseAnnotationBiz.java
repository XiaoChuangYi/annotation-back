package com.malgo.biz.brat.exercise;

import com.alibaba.fastjson.JSONObject;
import com.malgo.biz.BaseBiz;
import com.malgo.dao.UserExerciseRepository;
import com.malgo.dto.AnnotationExercise;
import com.malgo.entity.AnnotationCombine;
import com.malgo.entity.UserExercise;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.exercise.ListExerciseAnnotationRequest;
import com.malgo.result.PageVO;
import com.malgo.service.ExerciseAnnotationService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.ExerciseAnnotationBratVO;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/3.
 */
@Component
public class ListExerciseAnnotationBiz extends
    BaseBiz<ListExerciseAnnotationRequest, PageVO<ExerciseAnnotationBratVO>> {


  private final ExerciseAnnotationService exerciseAnnotationService;
  private final UserExerciseRepository userExerciseRepository;

  public ListExerciseAnnotationBiz(ExerciseAnnotationService exerciseAnnotationService,
      UserExerciseRepository userExerciseRepository) {
    this.exerciseAnnotationService = exerciseAnnotationService;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(ListExerciseAnnotationRequest listExerciseAnnotationRequest)
      throws InvalidInputException {
      if(listExerciseAnnotationRequest==null){
        throw new InvalidInputException("invalid-request","无效的请求");
      }
      if(listExerciseAnnotationRequest.getPageIndex()<1) {
        throw new InvalidInputException("invalid-pageIndex", "pageIndex应该大于等于1");
      }
      if(listExerciseAnnotationRequest.getPageSize()<=0) {
        throw new InvalidInputException("invalid-pageSize", "pageSize应该大于等于1");
      }
  }

  @Override
  protected void authorize(int userId, int role,
      ListExerciseAnnotationRequest listExerciseAnnotationRequest) throws BusinessRuleException {

  }

  @Override
  protected PageVO<ExerciseAnnotationBratVO> doBiz(
      ListExerciseAnnotationRequest listExerciseAnnotationRequest) {
    listExerciseAnnotationRequest.setPageIndex(listExerciseAnnotationRequest.getPageIndex()-1);
    Page page = exerciseAnnotationService
        .listStandardExerciseAnnotation(listExerciseAnnotationRequest);
    if (page.getContent().size() > 0) {
      List<AnnotationCombine> annotationCombineList = page.getContent();
      List<ExerciseAnnotationBratVO> exerciseAnnotationBratVOList = new LinkedList<>();
      for (AnnotationCombine annotationCombine : annotationCombineList) {
        ExerciseAnnotationBratVO exerciseAnnotationBratVO = new ExerciseAnnotationBratVO();
        BeanUtils.copyProperties(annotationCombine, exerciseAnnotationBratVO);
        JSONObject finalJson = AnnotationConvert
            .convertAnnotation2BratFormat(annotationCombine.getTerm(),
                annotationCombine.getFinalAnnotation());
        exerciseAnnotationBratVO.setFinalJson(finalJson);
        exerciseAnnotationBratVOList.add(exerciseAnnotationBratVO);
      }
      if (listExerciseAnnotationRequest.getUserId() <= 0) {
        //查询出所有的标准答案被指派了几个用户
        List<AnnotationExercise> annotationExerciseList = userExerciseRepository
            .findByAnnotationId();
        Map<Integer, Long> map = new HashMap<>();
        for (AnnotationExercise current : annotationExerciseList) {
          map.put(current.getAnnotationId(), current.getNum());
        }
        exerciseAnnotationBratVOList.stream().forEach(exerciseAnnotationBratVO ->
            exerciseAnnotationBratVO.setNum(map.get(exerciseAnnotationBratVO.getId()))
        );
      }
      if (listExerciseAnnotationRequest.getUserId() > 0) {
        List<UserExercise> userExerciseList = userExerciseRepository
            .findAllByAssigneeEquals(listExerciseAnnotationRequest.getUserId());
        exerciseAnnotationBratVOList.stream().forEach(exerciseAnnotationBratVO ->
            {
              if (userExerciseList.stream().filter(
                  userExercise -> userExercise.getAnnotationId() == exerciseAnnotationBratVO.getId())
                  .count() > 0) {
                  exerciseAnnotationBratVO.setDesignate(true);
              }else {
                exerciseAnnotationBratVO.setDesignate(false);
              }
            }
        );
      }
      PageVO<ExerciseAnnotationBratVO> pageVO = new PageVO<>();
      pageVO.setTotal(page.getTotalElements());
      pageVO.setDataList(exerciseAnnotationBratVOList);
      return pageVO;
    }
    return null;
  }
}
