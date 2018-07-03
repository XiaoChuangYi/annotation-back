package cn.malgo.annotation.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class AnnotationTaskDocBlockId implements Serializable {
  @Column(name = "task_doc_id")
  @NonNull
  private AnnotationTaskDocId taskDocId;

  @Column(name = "block_id")
  @NonNull
  private int blockId;
}
