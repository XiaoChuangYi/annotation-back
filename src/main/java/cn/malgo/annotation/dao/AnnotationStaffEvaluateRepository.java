package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnotationStaffEvaluateRepository
    extends JpaRepository<AnnotationStaffEvaluate, Integer>,
        JpaSpecificationExecutor<AnnotationStaffEvaluate> {

  AnnotationStaffEvaluate findByTaskIdEqualsAndAssigneeEqualsAndWorkDayEquals(
      int taskId, int assignee, java.sql.Date workDay);
}
