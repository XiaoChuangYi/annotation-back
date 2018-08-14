package cn.malgo.annotation.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FixAnnotationResult {
  private boolean success;
  private String msg;
}
