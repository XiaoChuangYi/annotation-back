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
    name = "personal_annotated_total_word_num_record",
    indexes = {
      @Index(
          name = "idx_task_id_assignee_id",
          columnList = "`assignee_id`,`task_id`",
          unique = true)
    })
public class PersonalAnnotatedTotalWordNumRecord extends BaseEntity {

  @Column(name = "task_id", nullable = false, columnDefinition = "bigint(20) default 0")
  private long taskId;

  @Column(name = "assignee_id", nullable = false, columnDefinition = "bigint(20) default 0")
  private long assigneeId;

  @Column(
      name = "annotated_total_word_num",
      nullable = false,
      columnDefinition = "int(11) default 0")
  private int annotatedTotalWordNum; // 批次总字数
}
