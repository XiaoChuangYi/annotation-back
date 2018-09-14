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

  List<AnnotationNew> findAllByStateInAndAnnotationTypeIn(
      List<AnnotationStateEnum> annotationStateEnums,
      List<AnnotationTypeEnum> annotationTypeEnums,
      Sort sort);

  @Query(
      value =
          "select annotation_new.* from annotation_new left join annotation_task_block on annotation_new.block_id = annotation_task_block.id where annotation_new.state = 'PRE_CLEAN' and annotation_new.delete_token = 0 and annotation_task_block.state in ('PRE_CLEAN', 'FINISHED') AND annotation_task_block.annotation_type IN (?1)",
      nativeQuery = true)
  List<AnnotationNew> findAllPreClean(List<AnnotationTypeEnum> annotationTypeEnums);

  List<AnnotationNew> findAllByTaskIdEqualsAndBlockIdIn(long taskId, List<Long> blockIds);

  List<AnnotationNew> findAllByBlockIdIn(Set<Long> blockIds);

  @Query(
      value = "select ac.state, count(ac.id) as num from annotation_new ac group by ac.state",
      nativeQuery = true)
  List<AnnotationSummary> findByStateGroup();

  Integer countAllByAnnotationTypeInAndState(
      List<AnnotationTypeEnum> annotationTypes, AnnotationStateEnum state);

  Integer countAllByStateIn(AnnotationStateEnum state);

  AnnotationNew findByTermEquals(String text);

  List<AnnotationNew> findAllByTaskIdAndAssigneeAndStateIn(
      long taskId, long assigneeId, List<AnnotationStateEnum> annotationStateEnums);

  List<AnnotationNew> findByTaskIdAndStateIn(
      long taskId, List<AnnotationStateEnum> annotationStateEnums);

  List<AnnotationNew> findByTaskId(long taskId);

  List<AnnotationNew> findByAssigneeAndStateIn(
      long assigneeId, List<AnnotationStateEnum> annotationStateEnums);

  List<AnnotationNew> findByTaskIdEqualsAndStateNotIn(
      long taskId, List<AnnotationStateEnum> annotationStateEnums);
}
