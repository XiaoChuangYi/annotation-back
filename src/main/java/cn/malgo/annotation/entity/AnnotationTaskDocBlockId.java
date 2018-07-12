package cn.malgo.annotation.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AnnotationTaskDocBlockId implements Serializable {
  @Column(name = "task_doc_id", nullable = false)
  private int taskDocId;

  @Column(name = "block_id", nullable = false)
  private int blockId;

  @Column(name = "block_order", nullable = false, updatable = false)
  private int order;
}
