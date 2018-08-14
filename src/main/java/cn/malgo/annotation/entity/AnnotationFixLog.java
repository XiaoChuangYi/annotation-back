package cn.malgo.annotation.entity;

import cn.malgo.service.entity.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 *
 *
 * <pre>
 * ALTER TABLE annotation_fix_log ADD COLUMN unique_combined_id VARCHAR(64);
 * UPDATE annotation_fix_log
 * SET unique_combined_id = CONCAT(annotation_id, '-', start, '-', end);
 * CREATE INDEX `idx_unique_combined_id`
 *   ON annotation_fix_log (`unique_combined_id`);
 * CREATE TRIGGER insert_trigger
 *   BEFORE INSERT
 *   ON annotation_fix_log
 *   FOR EACH ROW
 *   SET new.unique_combined_id = CONCAT(new.annotation_id, '-', new.start, '-', new.end);
 *
 * CREATE TRIGGER update_trigger
 *   BEFORE UPDATE
 *   ON annotation_fix_log
 *   FOR EACH ROW
 *   SET new.unique_combined_id = CONCAT(new.annotation_id, '-', new.start, '-', new.end);
 * </pre>
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "annotation_fix_log",
    indexes = {
      @Index(name = "idx_annotation_id", columnList = "annotation_id"),
      @Index(name = "idx_start", columnList = "start"),
      @Index(name = "idx_end", columnList = "end"),
    })
@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationFixLog extends BaseEntity {
  @Column(name = "annotation_id", nullable = false)
  private long annotationId;

  @Column(name = "start", nullable = false)
  private int start;

  @Column(name = "end", nullable = false)
  private int end;

  @Column(name = "state", nullable = false)
  private String state;

  public String getUniqueKey() {
    return this.annotationId + "-" + this.start + "-" + this.end;
  }
}
