package cn.malgo.annotation.dao;

import cn.malgo.annotation.dto.AnnotationSummary;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AnnotationRepository
    extends JpaRepository<AnnotationNew, Long>, JpaSpecificationExecutor<AnnotationNew> {

  List<AnnotationNew> findAllByAnnotationTypeInAndStateEquals(
      List<Integer> annotationTypeList, String state, Pageable pageable);

  Set<AnnotationNew> findAllByStateInAndBlockIdIn(
      List<AnnotationStateEnum> annotationStateEnums, List<Long> blockIds);

  List<AnnotationNew> findAllByStateIn(List<AnnotationStateEnum> annotationStateEnums, Sort sort);

  List<AnnotationNew> findAllByTaskIdEqualsAndBlockIdIn(long taskId, List<Long> blockIds);

  List<AnnotationNew> findAllByBlockIdIn(Set<Long> blockIds);

  @Query(
      value = "select ac.state, count(ac.id) as num from annotation_new ac group by ac.state",
      nativeQuery = true)
  List<AnnotationSummary> findByStateGroup();

  Integer countAllByAnnotationTypeInAndStateEquals(
      List<AnnotationTypeEnum> annotationTypes, AnnotationStateEnum state);

  Integer countAllByStateIn(AnnotationStateEnum state);

  AnnotationNew findByTermEquals(String text);

  Set<AnnotationNew> findAllByTaskIdEqualsAndAssigneeEquals(long taskId, long assigneeId);

  List<AnnotationNew> findAllByTaskIdEqualsAndAssigneeEqualsAndStateIn(
      long taskId, long assigneeId, List<AnnotationStateEnum> annotationStateEnums);

  List<AnnotationNew> findByTaskIdEqualsAndStateIn(
      long taskId, List<AnnotationStateEnum> annotationStateEnums);

  List<AnnotationNew> findByAssigneeEqualsAndStateIn(
      long assigneeId, List<AnnotationStateEnum> annotationStateEnums);
}
