package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnotationStaffEvaluateRepository
    extends JpaRepository<AnnotationStaffEvaluate, Long>,
        JpaSpecificationExecutor<AnnotationStaffEvaluate> {

  AnnotationStaffEvaluate findByTaskIdAndAssigneeAndWorkDay(
      long taskId, long assignee, java.sql.Date workDay);
}
