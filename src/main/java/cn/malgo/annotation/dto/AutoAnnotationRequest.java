package cn.malgo.annotation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoAnnotationRequest {
  private long id;
  private String text;
}
