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
import java.util.Collections;
import java.util.List;

@Entity
@Table(
    name = "annotation_task",
    indexes = {
      @Index(name = "idx_name", columnList = "name"),
      @Index(name = "idx_state", columnList = "state"),
    })
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"taskDocs"})
@JSONType(ignores = {"createdTime", "lastModified", "taskDocs"})
public class AnnotationTask {
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
  @Getter
  private Timestamp createdTime;

  @LastModifiedDate
  @Column(
      name = "last_modified",
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Getter
  private Timestamp lastModified;

  @Column(name = "name", nullable = false, length = 128)
  @Getter
  @Setter
  @NonNull
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 16)
  @Getter
  @Setter
  private AnnotationTaskState state = AnnotationTaskState.CREATED;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "task",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  @Getter
  private List<AnnotationTaskDoc> taskDocs = new ArrayList<>();

  public AnnotationTaskDoc addDoc(final OriginalDoc doc, final AnnotationTypeEnum annotationType) {
    final AnnotationTaskDoc taskDoc = new AnnotationTaskDoc(this, doc, annotationType);
    this.taskDocs.add(taskDoc);
    doc.getTasks().add(taskDoc);
    return taskDoc;
  }

  public AnnotationTask(int id) {
    this.id = id;
  }
}
