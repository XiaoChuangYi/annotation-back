package cn.malgo.annotation.dao;

import cn.malgo.annotation.dto.AnnotationExercise;
import cn.malgo.annotation.dto.AnnotationSummary;
import cn.malgo.annotation.entity.UserExercise;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/** Created by cjl on 2018/5/29. */
public interface UserExerciseRepository
    extends JpaRepository<UserExercise, Integer>, JpaSpecificationExecutor {

  @Query(
    value = "SELECT count(id) as num,annotation_id from user_exercise GROUP BY annotation_id",
    nativeQuery = true
  )
  List<AnnotationExercise> findByAnnotationId();

  @Query(
    value =
        "SELECT count(id) as num,state from user_exercise WHERE 1=1 AND assignee=1? GROUP BY state",
    nativeQuery = true
  )
  List<AnnotationSummary> findByAssigneeAndStateGroup(int assignee);

  List<UserExercise> findAllByAssigneeEquals(int assignee);

  Page<UserExercise> findUserExercisesByAssigneeEquals(int assignee, Pageable pageable);
}
