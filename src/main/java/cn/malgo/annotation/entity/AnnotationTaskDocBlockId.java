package cn.malgo.annotation.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class AnnotationTaskDocBlockId implements Serializable {
  @Column(name = "task_doc_id", nullable = false)
  @NonNull
  private int taskDocId;

  @Column(name = "block_id", nullable = false)
  @NonNull
  private int blockId;
}
