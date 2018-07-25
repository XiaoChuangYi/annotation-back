package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.service.entity.BaseEntity;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
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
@ToString(
    exclude = {"taskDocs", "taskBlocks"},
    callSuper = true)
@JSONType(ignores = {"taskDocs", "taskBlocks"})
public class AnnotationTaskBlock extends BaseEntity {

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

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "block", orphanRemoval = true)
  @Getter
  private List<AnnotationTaskDocBlock> taskDocs = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "block", orphanRemoval = true)
  @Getter
  private List<TaskBlock> taskBlocks = new ArrayList<>();
}
