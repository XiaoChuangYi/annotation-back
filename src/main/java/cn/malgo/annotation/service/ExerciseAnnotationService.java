package cn.malgo.annotation.service;

import cn.malgo.annotation.request.exercise.ListExerciseAnnotationRequest;
import org.springframework.data.domain.Page;

/** Created by cjl on 2018/6/3. */
public interface ExerciseAnnotationService {

  /** 条件查询习题标准答案标注 */
  Page listStandardExerciseAnnotation(ListExerciseAnnotationRequest listExerciseAnnotationRequest);
}
