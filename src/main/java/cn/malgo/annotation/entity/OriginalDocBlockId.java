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
public class OriginalDocBlockId implements Serializable {
  @Column(name = "doc_id", nullable = false)
  private long docId;

  @Column(name = "block_id", nullable = false)
  private long blockId;

  @Column(name = "block_order", nullable = false, updatable = false)
  private int order;
}
