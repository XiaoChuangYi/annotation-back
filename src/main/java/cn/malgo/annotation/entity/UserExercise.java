package cn.malgo.annotation.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Created by cjl on 2018/5/29. */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExercise extends BaseEntity {

  @Column(name = "user_annotation", nullable = false, columnDefinition = "MEDIUMTEXT")
  private String userAnnotation;

  private String state;
  private int annotationId;
}
