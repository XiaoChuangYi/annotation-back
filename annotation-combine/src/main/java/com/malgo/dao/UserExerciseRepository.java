package com.malgo.dao;

import com.malgo.dto.AnnotationExercise;
import com.malgo.entity.UserExercise;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by cjl on 2018/5/29.
 */
public interface UserExerciseRepository extends JpaRepository<UserExercise, Integer>,
    JpaSpecificationExecutor {

  @Query(value = "SELECT count(id) as num,annotation_id from user_exercise GROUP BY annotation_id",nativeQuery = true)
  List<AnnotationExercise> findByAnnotationId();

  List<UserExercise> findAllByAssigneeEquals(int assignee);

  Page<UserExercise> findAllByAssigneeEquals(int assignee,Pageable pageable);

}
