package cn.malgo.annotation.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class TaskBlockId implements Serializable {
  @Column(name = "task_id", nullable = false)
  private long taskId;

  @Column(name = "block_id", nullable = false)
  private long blockId;
}
