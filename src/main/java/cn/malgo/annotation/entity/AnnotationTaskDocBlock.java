package cn.malgo.annotation.entity;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(
    name = "annotation_task_doc_block",
    indexes = {
      @Index(name = "idx_order", columnList = "block_order"),
    })
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"taskDoc", "block"})
@JSONType(ignores = {"createdTime", "lastModified", "taskDoc", "block"})
@Data
public class AnnotationTaskDocBlock {
  @EmbeddedId private AnnotationTaskDocBlockId id;

  @CreatedDate
  @Column(
      name = "created_time",
      updatable = false,
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp createdTime;

  @LastModifiedDate
  @Column(
      name = "last_modified",
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp lastModified;

  @ManyToOne(fetch = FetchType.EAGER /*, cascade = { CascadeType.MERGE }*/)
  @MapsId("taskDocId")
  @JoinColumn(name = "task_doc_id")
  private AnnotationTaskDoc taskDoc;

  @ManyToOne(fetch = FetchType.LAZY /*, cascade = { CascadeType.MERGE }*/)
  @MapsId("blockId")
  @JoinColumn(name = "block_id")
  private AnnotationTaskBlock block;

  public AnnotationTaskDocBlock(
      final AnnotationTaskDoc taskDoc, final AnnotationTaskBlock block, final int order) {
    this.taskDoc = taskDoc;
    this.block = block;
    this.id = new AnnotationTaskDocBlockId(taskDoc.getId(), block.getId(), order);
  }
}
