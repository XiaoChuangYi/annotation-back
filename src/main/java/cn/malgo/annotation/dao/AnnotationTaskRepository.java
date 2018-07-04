package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnotationTaskRepository
    extends JpaRepository<AnnotationTask, Integer>, JpaSpecificationExecutor {}
