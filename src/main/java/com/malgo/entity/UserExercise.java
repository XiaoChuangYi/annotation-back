package com.malgo.entity;

import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/29.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserExercise extends BaseEntity {
  private String term;
  private String userAnnotation;
  private String state;
  private int annotationId;
}
