package cn.malgo.annotation.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "annotation_staff_evaluate",
    indexes = {
      @Index(
          name = "unique_task_id_assignee_work_day",
          columnList = "task_id,assignee,work_day",
          unique = true)
    })
@EntityListeners(AuditingEntityListener.class)
public class AnnotationStaffEvaluate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "gmt_created", updatable = false, nullable = false)
  @CreatedDate
  private Date gmtCreated;

  @Column(name = "gmt_modified", nullable = false)
  @LastModifiedDate
  private Date gmtModified;

  @Column(name = "task_id", nullable = false, columnDefinition = "int(11) default 0")
  private int taskId;

  @Column(name = "task_name", nullable = false, columnDefinition = "varchar(1024)")
  private String taskName;

  @Column(name = "assignee", nullable = false, columnDefinition = "int(11) default 0")
  private int assignee;

  @Column(name = "work_day", columnDefinition = "Date")
  private java.sql.Date workDay;

  @Column(name = "total_branch_num", nullable = false, columnDefinition = "int(11) default 0")
  private int totalBranchNum; // 批次总条数

  @Column(name = "total_word_num", nullable = false, columnDefinition = "int(11) default 0")
  private int totalWordNum; // 批次总字数

  @Column(
      name = "current_day_annotated_branch_num",
      nullable = false,
      columnDefinition = "int(11) default 0")
  private int currentDayAnnotatedBranchNum;

  @Column(
      name = "current_day_annotated_word_num",
      nullable = false,
      columnDefinition = "int(11) default 0")
  private int currentDayAnnotatedWordNum; // 已标注字数

  @Column(name = "rest_branch_num", nullable = false, columnDefinition = "int(11) default 0")
  private int restBranchNum;

  @Column(name = "rest_word_num", nullable = false, columnDefinition = "int(11) default 0")
  private int restWordNum;

  @Column(name = "in_conformity", nullable = false, columnDefinition = "double default 0")
  private double inConformity;

  @Column(name = "abandon_branch_num", nullable = false, columnDefinition = "int(11) default 0")
  private int abandonBranchNum;

  @Column(name = "abandon_word_num", nullable = false, columnDefinition = "int(11) default 0")
  private int abandonWordNum;

  public AnnotationStaffEvaluate(
      int taskId,
      String taskName,
      int assignee,
      java.sql.Date workDay,
      int totalBranchNum,
      int totalWordNum,
      int currentDayAnnotatedBranchNum,
      int currentDayAnnotatedWordNum,
      int restBranchNum,
      int restWordNum,
      double inConformity,
      int abandonBranchNum,
      int abandonWordNum) {
    this.taskId = taskId;
    this.taskName = taskName;
    this.assignee = assignee;
    this.workDay = workDay;
    this.totalBranchNum = totalBranchNum;
    this.totalWordNum = totalWordNum;
    this.currentDayAnnotatedBranchNum = currentDayAnnotatedBranchNum;
    this.currentDayAnnotatedWordNum = currentDayAnnotatedWordNum;
    this.restBranchNum = restBranchNum;
    this.restWordNum = restWordNum;
    this.inConformity = inConformity;
    this.abandonBranchNum = abandonBranchNum;
    this.abandonWordNum = abandonWordNum;
  }
}
