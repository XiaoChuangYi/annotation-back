package com.malgo.dao;

import com.malgo.entity.AnnotationCombine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by cjl on 2018/5/29.
 */
public interface UserExerciseRepository extends JpaRepository<AnnotationCombine,Integer>,JpaSpecificationExecutor {

}
