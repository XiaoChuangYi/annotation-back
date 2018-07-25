package cn.malgo.annotation.entity;

import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.service.entity.BaseEntity;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
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
    name = "annotation_task",
    indexes = {
      @Index(name = "idx_name", columnList = "name"),
      @Index(name = "idx_state", columnList = "state"),
    })
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(
    exclude = {"taskBlocks"},
    callSuper = true)
@JSONType(ignores = {"taskBlocks"})
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
  private List<TaskBlock> taskBlocks = new ArrayList<>();

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

  @Column(name = "precision_rate", nullable = false, columnDefinition = "double default 0")
  @Getter
  @Setter
  private double precisionRate = 0; // 批次准确率

  @Column(name = "recall_rate", nullable = false, columnDefinition = "double default 0")
  @Getter
  @Setter
  private double recallRate = 0; // 批次召回率

  public TaskBlock addBlock(final AnnotationTaskBlock block) {
    final TaskBlock taskBlock = new TaskBlock(this, block);
    taskBlocks.add(taskBlock);
    //    block.getTaskBlocks().add(taskBlock);
    return taskBlock;
  }
}
