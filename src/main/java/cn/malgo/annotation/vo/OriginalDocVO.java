package cn.malgo.annotation.vo;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class OriginalDocVO {
  private int id; // 文档ID
  private String type; // 文档类型
  private String state; // 文档状态
}
