package cn.malgo.annotation.dao;

import cn.malgo.annotation.dto.AnnotationSummary;
import cn.malgo.annotation.entity.AnnotationCombine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnnotationCombineRepository
    extends JpaRepository<AnnotationCombine, Integer>, JpaSpecificationExecutor {

  default List<AnnotationCombine> findAllByIdInAndIsTaskEquals(List<Integer> idList) {
    return findAllByIdInAndIsTaskEquals(idList, 0);
  }

  List<AnnotationCombine> findAllByIdInAndIsTaskEquals(List<Integer> idList, int task);

  List<AnnotationCombine> findAllByAnnotationTypeInAndStateEqualsAndIsTaskEquals(
      List<Integer> annotationTypeList, String state, Pageable pageable, int task);

  @Query(
      value =
          "select ac.state, count(ac.id) as num from annotation_combine ac where ac.is_task = 0 group by ac.state",
      nativeQuery = true)
  List<AnnotationSummary> findByStateGroup();

  @Query(
      value =
          "select ac.state, count(ac.id) as num from annotation_combine ac where ac.is_task = 0 and ac.assignee= ?1 group by ac.state",
      nativeQuery = true)
  List<AnnotationSummary> findByAssigneeAndStateGroup(int assignee);

  Integer countAllByAnnotationTypeInAndStateEquals(List<Integer> annotationTypes, String state);

  Integer countAllByStateIn(String state);

  List<AnnotationCombine> findByAnnotationTypeAndIdBetweenAndStateIn(
      int annotationType, int startId, int endId, List<String> states);

  AnnotationCombine findByTermEquals(String text);
}
