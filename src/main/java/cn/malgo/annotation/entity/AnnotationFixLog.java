package cn.malgo.annotation.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.sql.Timestamp;

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
@Table(
    name = "annotation_fix_log",
    indexes = {
      @Index(name = "unique_fix_log", columnList = "annotation_id,start,end", unique = true),
      @Index(name = "idx_annotation_id", columnList = "annotation_id"),
      @Index(name = "idx_start", columnList = "start"),
      @Index(name = "idx_end", columnList = "end"),
    })
@Data
public class AnnotationFixLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @CreatedDate
  @Column(
      name = "created_time",
      updatable = false,
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp createdTime;

  @LastModifiedDate
  @Column(
      name = "modified_time",
      nullable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp modifiedTime;

  @Column(name = "annotation_id", nullable = false)
  private int annotationId;

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
