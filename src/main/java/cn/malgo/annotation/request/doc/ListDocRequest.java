package cn.malgo.annotation.request.doc;

import lombok.Data;

@Data
public class ListDocRequest {
  private int pageIndex;
  private int pageSize;
  private int minTextLength;
  private String name; // 文档名字
  private String source; // 文档来源
  private String text; // 文档内容
  private String type; // 文档类型
}
