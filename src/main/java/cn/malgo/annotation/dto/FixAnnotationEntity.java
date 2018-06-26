package cn.malgo.annotation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FixAnnotationEntity {
  private String type;
  private String term;
}
