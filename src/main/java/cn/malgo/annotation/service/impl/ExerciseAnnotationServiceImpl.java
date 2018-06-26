package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.UserExerciseRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.exercise.ListExerciseAnnotationRequest;
import cn.malgo.annotation.service.ExerciseAnnotationService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/** Created by cjl on 2018/6/3. */
@Service
@Slf4j
public class ExerciseAnnotationServiceImpl implements ExerciseAnnotationService {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public ExerciseAnnotationServiceImpl(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  /** spring-boot-jpa 自定义查询 */
  private static Specification<AnnotationCombine> queryExerciseAnnotationCondition(
      ListExerciseAnnotationRequest param) {
    return (Specification<AnnotationCombine>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          predicates.add(criteriaBuilder.equal(root.get("isTask"), 1));
          if (param.getAnnotationTypes() != null && param.getAnnotationTypes().size() > 0) {
            predicates.add(
                criteriaBuilder.in(root.get("annotationType")).value(param.getAnnotationTypes()));
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
  }

  /** 条件查询习题标准答案标注 */
  @Override
  public Page<AnnotationCombine> listStandardExerciseAnnotation(
      ListExerciseAnnotationRequest listExerciseAnnotationRequest) {
    Page<AnnotationCombine> page =
        annotationCombineRepository.findAll(
            queryExerciseAnnotationCondition(listExerciseAnnotationRequest),
            PageRequest.of(
                listExerciseAnnotationRequest.getPageIndex(),
                listExerciseAnnotationRequest.getPageSize()));
    return page;
  }
}
