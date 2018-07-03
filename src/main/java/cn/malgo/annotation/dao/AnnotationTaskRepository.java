package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnotationTaskRepository extends JpaRepository<AnnotationTask, Integer> {}
