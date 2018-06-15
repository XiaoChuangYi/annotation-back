package com.malgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by cjl on 2018/5/31.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoAnnotationRequest {
  private int id;
  private String text;
}
