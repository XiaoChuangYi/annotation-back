package cn.malgo.annotation.entity;

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

  private int isTask;

  private int blockId;

  @Column(columnDefinition = "text")
  private String comment;

  @Transient private String userName;
}
