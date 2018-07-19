package cn.malgo.annotation.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FixAnnotationEntity {
  private String type;
  private String term;
}
