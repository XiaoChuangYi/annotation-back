package com.malgo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/29.
 */
@Entity
@Table(name = "annotation_combine")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationCombine extends BaseEntity {


  @Column(name = "final_annotation", nullable = false)
  private String finalAnnotation;
  @Column(name = "manual_annotation", nullable = false)
  private String manualAnnotation;
  @Column(name = "reviewed_annotation", nullable = false)
  private String reviewedAnnotation;
  private String state;
  private int isTask;

}
