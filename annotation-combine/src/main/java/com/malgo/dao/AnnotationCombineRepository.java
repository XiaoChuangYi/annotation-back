package com.malgo.dao;

import com.malgo.entity.AnnotationCombine;
import com.malgo.dto.AnnotationSummary;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by cjl on 2018/5/29.
 */
public interface AnnotationCombineRepository extends JpaRepository<AnnotationCombine,Integer>,JpaSpecificationExecutor {

  List<AnnotationCombine> findAllByIdInAndIsTaskEquals(List<Integer> idList,int task);

  List<AnnotationCombine> findAllByAnnotationTypeInAndStateEqualsAndIsTaskEquals(List<Integer> annotationTypeList,String state,Pageable pageable,int task);

  @Query(value = "select ac.state,count(ac.id) as num from annotation_combine ac where ac.is_task=0  group by ac.state",nativeQuery = true)
  List<AnnotationSummary> findByStateGroup();

  Integer countAllByAnnotationTypeInAndStateEquals(List<Integer> annotationTypes,String state);

  Integer countAllByStateIn(String state);

}
