package com.malgo.biz.brat.exercise;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dao.UserExerciseRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.entity.UserExercise;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.DesignateAnnotationRequest;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/6/4.
 */
@Component
public class DesignateUserExerciseBiz extends BaseBiz<DesignateAnnotationRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final UserExerciseRepository userExerciseRepository;


  public DesignateUserExerciseBiz(AnnotationCombineRepository annotationCombineRepository,
      UserExerciseRepository userExerciseRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  protected void validateRequest(DesignateAnnotationRequest designateAnnotationRequest)
      throws InvalidInputException {
    if (designateAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (designateAnnotationRequest.getUserId() <= 0) {
      throw new InvalidInputException("invalid-userId", "无效的userId");
    }
  }

  @Override
  protected void authorize(int userId, int role,
      DesignateAnnotationRequest designateAnnotationRequest) throws BusinessRuleException {
      if(role>2){
        throw new BusinessRuleException("no-permission-execute","您没有权限执行该操作！");
      }
  }

  @Override
  protected Object doBiz(DesignateAnnotationRequest designateAnnotationRequest) {
    List<AnnotationCombine> annotationCombineList = annotationCombineRepository
        .findAllByIdInAndIsTaskEquals(designateAnnotationRequest.getIdList(), 1);
    //再加一层过滤，如果前端把已经指派过的标准习题集再次指派，则过滤该id
    List<UserExercise> userExerciseList = userExerciseRepository
        .findAllByAssigneeEquals(designateAnnotationRequest.getUserId());
    Iterator<AnnotationCombine> iterator=annotationCombineList.iterator();
    while (iterator.hasNext()){
      AnnotationCombine annotationCombine=iterator.next();
      if(userExerciseList.stream().filter(userExercise ->
          userExercise.getAnnotationId()==annotationCombine.getId()
      ).count()>0){
        iterator.remove();
      }
    }
    List<UserExercise> finalUserExerciseList = new LinkedList<>();
    IntStream.range(0, annotationCombineList.size()).forEach((int i) -> {
          final UserExercise current = new UserExercise();
          current.setState(AnnotationCombineStateEnum.unDistributed.name());
          current.setUserAnnotation(annotationCombineList.get(i).getFinalAnnotation());
          current.setAnnotationId(annotationCombineList.get(i).getId());
          current.setAssignee(designateAnnotationRequest.getUserId());
          current.setAnnotationType(annotationCombineList.get(i).getAnnotationType());
          finalUserExerciseList.add(current);
        }
    );
    if (finalUserExerciseList.size() > 0) {
      userExerciseRepository.saveAll(finalUserExerciseList);
    }
    return null;
  }
}
