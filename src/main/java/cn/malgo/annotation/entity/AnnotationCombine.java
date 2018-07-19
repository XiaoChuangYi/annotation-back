package cn.malgo.annotation.entity;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "annotation_combine")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationCombine extends BaseEntity {

  @Column(name = "final_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String finalAnnotation = "";

  @Column(name = "manual_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String manualAnnotation = "";

  @Column(name = "reviewed_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String reviewedAnnotation = "";

  @Column(name = "is_task")
  private int isTask;

  @Column(name = "block_id")
  private Integer blockId;

  @Column(name = "comment", columnDefinition = "text")
  private String comment;

  @Column(name = "commit_timestamp", columnDefinition = "Date default '1000-01-01'")
  private Date commitTimestamp;

  @Transient private String userName;
}
