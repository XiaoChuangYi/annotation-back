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
public class AnnotationTaskDocId implements Serializable {
  @Column(name = "task_id")
  @NonNull
  private int taskId;

  @Column(name = "doc_id")
  @NonNull
  private int docId;
}
