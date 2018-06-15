package com.malgo.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/31.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAnnotationAlgorithm {

  private int id;
  private String text;
  private String autoAnnotation;
  private String manualAnnotation;
  private List<NewTerm> newTerms;

}
