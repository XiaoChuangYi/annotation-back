package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.service.entity.BaseEntity;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "annotation_task_block",
    indexes = {
      @Index(name = "idx_annotation_type", columnList = "annotation_type"),
      @Index(name = "idx_state", columnList = "state"),
      @Index(columnList = "annotation_type,state"),
      @Index(columnList = "ner_fresh_rate"),
    })
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(
    exclude = {"docBlocks", "taskBlocks"},
    callSuper = true)
@JSONType(ignores = {"docBlocks", "taskBlocks", "nerResult"})
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

  @Column(name = "ner_result", columnDefinition = "MEDIUMTEXT")
  @Getter
  @Setter
  private String nerResult;

  /** 实体在之前的实体中未出现的比例 */
  @Column(name = "ner_fresh_rate", columnDefinition = "double default 0", nullable = false)
  @Getter
  @Setter
  private double nerFreshRate;

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

  @Getter
  @Setter
  @Column(name = "assignee", nullable = false, columnDefinition = "BIGINT(20) default 0")
  private long assignee = 0L;

  @Getter
  @Setter
  @Column(name = "memo", columnDefinition = "varchar(512)")
  private String memo;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "doc", orphanRemoval = true)
  @Getter
  private List<OriginalDocBlock> docBlocks = new ArrayList<>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "block", orphanRemoval = true)
  @Getter
  private List<TaskBlock> taskBlocks = new ArrayList<>();
}
