package cn.malgo.annotation.entity;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
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
  }
)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"taskDoc", "block"})
@JSONType(ignores = {"createdTime", "lastModified", "taskDoc", "block"})
public class AnnotationTaskDocBlock {
  @EmbeddedId private AnnotationTaskDocBlockId id;

  @CreatedDate
  @Column(
    name = "created_time",
    updatable = false,
    nullable = false,
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
  )
  private Timestamp createdTime;

  @LastModifiedDate
  @Column(
    name = "last_modified",
    updatable = false,
    nullable = false,
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
  )
  private Timestamp lastModified;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("taskDocId")
  @NonNull
  @Getter
  private AnnotationTaskDoc taskDoc;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("blockId")
  @NonNull
  @Getter
  private AnnotationTaskBlock block;

  @Column(name = "block_order", nullable = false)
  @NonNull
  private int order;

  private AnnotationTaskDocBlock() {}

  public AnnotationTaskDocBlock(
      final AnnotationTaskDoc taskDoc, final AnnotationTaskBlock block, final int order) {
    this.taskDoc = taskDoc;
    this.block = block;
    this.order = order;
    this.id = new AnnotationTaskDocBlockId(taskDoc.getId(), block.getId());
  }
}
