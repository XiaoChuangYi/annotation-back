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
    name = "annotation_task_block",
    indexes = {
      @Index(name = "idx_annotation_type", columnList = "annotation_type"),
      @Index(name = "idx_state", columnList = "state"),
      //      @Index(name = "idx_unique_text", columnList = "annotation_type,text", unique = true),
    })
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "id")
@ToString
@JSONType(ignores = {"createdTime", "lastModified"})
public class AnnotationTaskBlock {
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
      updatable = false,
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp lastModified;

  @Column(name = "text", nullable = false, updatable = false, columnDefinition = "MEDIUMTEXT")
  @Getter
  @Setter
  @NonNull
  private String text;

  @Column(name = "annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  @Getter
  @Setter
  @NonNull
  private String annotation;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false, length = 16)
  @Getter
  @Setter
  private AnnotationTaskState state = AnnotationTaskState.CREATED;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "annotation_type", nullable = false, updatable = false)
  @Getter
  @Setter
  @NonNull
  private AnnotationTypeEnum annotationType;

  //  @OneToMany(
  //    fetch = FetchType.LAZY,
  //    mappedBy = "block",
  //    cascade = CascadeType.ALL,
  //    orphanRemoval = true
  //  )
  //  @Getter
  //  private List<AnnotationTaskDocBlock> taskDocs = new ArrayList<>();
}
