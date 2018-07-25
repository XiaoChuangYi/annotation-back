package cn.malgo.annotation.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import javax.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "task_block",
    indexes = {
      @Index(name = "idx_order", columnList = "block_order"),
    })
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"task", "block"})
@Getter
@Setter
public class TaskBlock {
  @EmbeddedId private TaskBlockId id;

  @CreatedDate
  @Column(
      name = "created_time",
      updatable = false,
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date createdTime;

  @LastModifiedDate
  @Column(
      name = "last_modified",
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @JSONField(format = "yyyy-MM-dd HH:mm:ss")
  private Date lastModified;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @MapsId("taskId")
  @JoinColumn(name = "task_id")
  private AnnotationTask task;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @MapsId("blockId")
  @JoinColumn(name = "block_id")
  private AnnotationTaskBlock block;
}
