package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.service.entity.BaseEntity;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "annotation_task",
    indexes = {
      @Index(name = "idx_name", columnList = "name"),
      @Index(name = "idx_state", columnList = "state"),
    })
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(
    exclude = {"taskDocs"},
    callSuper = true)
@JSONType(ignores = {"taskDocs"})
public class AnnotationTask extends BaseEntity {
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

  @Column(name = "total_branch_num", nullable = false, columnDefinition = "int(11) default 0")
  @Getter
  @Setter
  private int totalBranchNum = 0; // 批次总条数

  @Column(name = "total_word_num", nullable = false, columnDefinition = "int(11) default 0")
  @Getter
  @Setter
  private int totalWordNum = 0; // 批次总字数

  @Column(name = "annotated_branch_num", nullable = false, columnDefinition = "int(11) default 0")
  @Getter
  @Setter
  private int annotatedBranchNum = 0; // 批次已标注条数

  @Column(name = "annotated_word_num", nullable = false, columnDefinition = "int(11) default 0")
  @Getter
  @Setter
  private int annotatedWordNum = 0; // 批次已标注总字数

  @Column(name = "rest_branch_num", nullable = false, columnDefinition = "int(11) default 0")
  @Getter
  @Setter
  private int restBranchNum = 0; // 批次剩余条数

  @Column(name = "rest_word_num", nullable = false, columnDefinition = "int(11) default 0")
  @Getter
  @Setter
  private int restWordNum = 0; // 批次剩余标注总字数

  @Column(name = "in_conformity", nullable = false, columnDefinition = "double default 0")
  @Getter
  @Setter
  private double inConformity = 0; // 批次不一致率

  public AnnotationTaskDoc addDoc(final OriginalDoc doc, final AnnotationTypeEnum annotationType) {
    final AnnotationTaskDoc taskDoc = new AnnotationTaskDoc(this, doc, annotationType);
    this.taskDocs.add(taskDoc);
    doc.getTasks().add(taskDoc);
    return taskDoc;
  }
}
