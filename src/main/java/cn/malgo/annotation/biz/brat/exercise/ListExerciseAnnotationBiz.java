package cn.malgo.annotation.biz.brat.exercise;

import com.alibaba.fastjson.JSONObject;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.dto.AnnotationExercise;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.UserExercise;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.exercise.ListExerciseAnnotationRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.service.ExerciseAnnotationService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.ExerciseAnnotationBratVO;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/3. */
@Component
public class ListExerciseAnnotationBiz
    extends BaseBiz<ListExerciseAnnotationRequest, PageVO<ExerciseAnnotationBratVO>> {

  private final ExerciseAnnotationService exerciseAnnotationService;
  private final UserExerciseRepository userExerciseRepository;

  public ListExerciseAnnotationBiz(
      ExerciseAnnotationService exerciseAnnotationService,
      UserExerciseRepository userExerciseRepository) {
    this.exerciseAnnotationService = exerciseAnnotationService;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(ListExerciseAnnotationRequest listExerciseAnnotationRequest)
      throws InvalidInputException {
    if (listExerciseAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (listExerciseAnnotationRequest.getPageIndex() < 1) {
      throw new InvalidInputException("invalid-page-index", "pageIndex应该大于等于1");
    }
    if (listExerciseAnnotationRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "pageSize应该大于等于1");
    }
  }

  @Override
  protected PageVO<ExerciseAnnotationBratVO> doBiz(
      ListExerciseAnnotationRequest listExerciseAnnotationRequest) {
    listExerciseAnnotationRequest.setPageIndex(listExerciseAnnotationRequest.getPageIndex() - 1);
    Page page =
        exerciseAnnotationService.listStandardExerciseAnnotation(listExerciseAnnotationRequest);
    if (page.getContent().size() > 0) {
      // 查询出所有的标准答案
      List<AnnotationCombine> annotationCombineList = page.getContent();
      List<ExerciseAnnotationBratVO> exerciseAnnotationBratVOList = new LinkedList<>();
      for (AnnotationCombine annotationCombine : annotationCombineList) {
        ExerciseAnnotationBratVO exerciseAnnotationBratVO = new ExerciseAnnotationBratVO();
        BeanUtils.copyProperties(annotationCombine, exerciseAnnotationBratVO);
        JSONObject finalJson =
            AnnotationConvert.convertAnnotation2BratFormat(
                annotationCombine.getTerm(),
                annotationCombine.getFinalAnnotation(),
                annotationCombine.getAnnotationType());
        exerciseAnnotationBratVO.setFinalJson(finalJson);
        exerciseAnnotationBratVOList.add(exerciseAnnotationBratVO);
      }
      if (listExerciseAnnotationRequest.getUserId() <= 0) {
        // 查询出所有的标准答案被指派了几个用户
        List<AnnotationExercise> annotationExerciseList =
            userExerciseRepository.findByAnnotationId();
        Map<Integer, Long> map = new HashMap<>();
        for (AnnotationExercise current : annotationExerciseList) {
          map.put(current.getAnnotationId(), current.getNum());
        }
        if (map.entrySet().size() > 0) {
          exerciseAnnotationBratVOList
              .stream()
              .forEach(
                  exerciseAnnotationBratVO ->
                      exerciseAnnotationBratVO.setNum(map.get(exerciseAnnotationBratVO.getId())));
        }
      }
      if (listExerciseAnnotationRequest.getUserId() > 0) {
        List<UserExercise> userExerciseList =
            userExerciseRepository.findAllByAssigneeEquals(
                listExerciseAnnotationRequest.getUserId());
        exerciseAnnotationBratVOList
            .stream()
            .forEach(
                exerciseAnnotationBratVO -> {
                  if (userExerciseList
                          .stream()
                          .filter(
                              userExercise ->
                                  userExercise.getAnnotationId()
                                      == exerciseAnnotationBratVO.getId())
                          .count()
                      > 0) {
                    exerciseAnnotationBratVO.setDesignate(true);
                  } else {
                    exerciseAnnotationBratVO.setDesignate(false);
                  }
                });
      }
      PageVO<ExerciseAnnotationBratVO> pageVO = new PageVO<>();
      pageVO.setTotal(page.getTotalElements());
      pageVO.setDataList(exerciseAnnotationBratVOList);
      return pageVO;
    }
    return null;
  }
}
