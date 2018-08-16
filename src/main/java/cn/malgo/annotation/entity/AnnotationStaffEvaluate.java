package cn.malgo.annotation.entity;

import cn.malgo.service.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(
    name = "annotation_staff_evaluate",
    indexes = {
      @Index(
          name = "unique_task_id_assignee_work_day",
          columnList = "task_id,assignee,work_day",
          unique = true)
    })
public class AnnotationStaffEvaluate extends BaseEntity {
  @Column(name = "task_id", nullable = false, columnDefinition = "bigint(20) default 0")
  private long taskId;

  @Column(name = "task_name", nullable = false, length = 1024)
  private String taskName;

  @Column(name = "work_day", columnDefinition = "Date")
  private java.sql.Date workDay;

  @Column(name = "assignee", nullable = false, columnDefinition = "bigint(20) default 0")
  private long assignee;

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

  @Column(name = "abandon_branch_num", nullable = false, columnDefinition = "int(11) default 0")
  private int abandonBranchNum;

  @Column(name = "abandon_word_num", nullable = false, columnDefinition = "int(11) default 0")
  private int abandonWordNum;

  @Column(name = "precision_rate")
  private Double precisionRate; // 准确率

  @Column(name = "recall_rate")
  private Double recallRate; // 召回率
}
