package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "annotation_task_doc",
    indexes = {
      @Index(name = "idx_annotation_type", columnList = "annotation_type"),
      @Index(name = "idx_state", columnList = "state"),
    })
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"task", "doc", "blocks"})
@JSONType(ignores = {"createdTime", "lastModified", "task", "doc", "blocks"})
public class AnnotationTaskDoc {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Getter
  private int id;

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

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "task_id")
  @Setter(AccessLevel.PACKAGE)
  @Getter
  private AnnotationTask task;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "doc_id")
  @Setter(AccessLevel.PACKAGE)
  @Getter
  private OriginalDoc doc;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "annotation_type", nullable = false)
  @Getter
  @Setter
  private AnnotationTypeEnum annotationType;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 16)
  @Getter
  @Setter
  private AnnotationTaskState state = AnnotationTaskState.CREATED;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "taskDoc",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @Getter
  @OrderBy("id.order")
  private List<AnnotationTaskDocBlock> blocks = new ArrayList<>();

  public AnnotationTaskDoc(
      final AnnotationTask task, final OriginalDoc doc, final AnnotationTypeEnum annotationType) {
    this.task = task;
    this.doc = doc;
    this.annotationType = annotationType;
  }

  public AnnotationTaskDocBlock addBlock(final AnnotationTaskBlock block, final int order) {
    if (block.getAnnotationType() != this.getAnnotationType()) {
      throw new IllegalArgumentException("invalid annotation type: " + block.getAnnotationType());
    }

    final AnnotationTaskDocBlock taskDocBlock = new AnnotationTaskDocBlock(this, block, order);
    blocks.add(taskDocBlock);
    return taskDocBlock;
  }
}
