package cn.malgo.annotation.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnotationSummaryVO {
  private String state;
  private long num;
}
