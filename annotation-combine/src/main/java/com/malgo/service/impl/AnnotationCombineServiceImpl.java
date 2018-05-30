package com.malgo.service.impl;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.request.DesignateAnnotationRequest;
import com.malgo.request.ListAnnotationCombineRequest;
import com.malgo.entity.AnnotationCombine;
import com.malgo.request.RandomDesignateAnnotationRequest;
import com.malgo.service.AnnotationCombineService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * Created by cjl on 2018/5/29.
 */
@Service
@Slf4j
public class AnnotationCombineServiceImpl implements AnnotationCombineService {

  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public AnnotationCombineServiceImpl(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  /**
   * spring-boot-jpa 自定义查询
   */
  private static Specification<AnnotationCombine> queryAnnotationCombineCondition(
      ListAnnotationCombineRequest param) {
    return (Specification<AnnotationCombine>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          predicates.add(criteriaBuilder.equal(root.get("isTask"),0));
          if (param.getAnnotationTypes() != null && param.getAnnotationTypes().size() > 0) {
            predicates.add(
                criteriaBuilder.in(root.get("annotationType")).value(param.getAnnotationTypes()));
          }
          if (param.getStates() != null && param.getStates().size() > 0) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(param.getStates()));
          }
          if (param.getUserId() > 0) {
            predicates.add(
                criteriaBuilder.equal(root.get("assignee"), param.getUserId()));
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
  }

  /**
   * 条件查询标注习题集
   */
  @Override
  public Page<AnnotationCombine> listAnnotationCombine(
      ListAnnotationCombineRequest listAnnotationCombineRequest) {
    return annotationCombineRepository
        .findAll(queryAnnotationCombineCondition(listAnnotationCombineRequest)
            , PageRequest.of(listAnnotationCombineRequest.getPageIndex(),
                listAnnotationCombineRequest.getPageSize(), Direction.DESC,"state"));
  }

  /**
   * 批量指派标注数据给特定用户
   */
  @Override
  public void designateAnnotationCombine(DesignateAnnotationRequest designateAnnotationRequest) {
    List<AnnotationCombine> annotationCombineList = annotationCombineRepository
        .findAllByIdInAndIsTaskEquals(designateAnnotationRequest.getIdList(),0);
    annotationCombineList.stream().forEach(
        annotationCombine -> {
          annotationCombine.setAssignee(designateAnnotationRequest.getUserId());
          annotationCombine.setState(AnnotationCombineStateEnum.preAnnotation.name());
        });
    annotationCombineRepository.saveAll(annotationCombineList);
  }

  /**
   * 随机批量指派标注数据给用户
   */
  @Override
  public void randomDesignateAnnotationCombine(
      RandomDesignateAnnotationRequest randomDesignateAnnotationRequest) {
    //第一步根据未分配状态，标注类型，以及num，查询出所有的标注
    List<AnnotationCombine> annotationCombineList = annotationCombineRepository
        .findAllByAnnotationTypeInAndStateEqualsAndIsTaskEquals(randomDesignateAnnotationRequest.getAnnotationTypes(),
            AnnotationCombineStateEnum.unDistributed.name()
            ,PageRequest.of(0,randomDesignateAnnotationRequest.getNum()),0);

    //第二步(假)随机更新对应的标注的assignee
    List<Integer> userIdList=randomDesignateAnnotationRequest.getUserIdList();
    IntStream.range(0,annotationCombineList.size()).forEach(i->
        {
          int k = i % userIdList.size();
          annotationCombineList.get(i).setAssignee(userIdList.get(k));
          annotationCombineList.get(i).setState(AnnotationCombineStateEnum.preAnnotation.name());
        }
    );
    annotationCombineRepository.saveAll(annotationCombineList);
  }
}
