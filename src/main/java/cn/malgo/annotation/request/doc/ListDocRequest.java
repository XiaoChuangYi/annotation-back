package cn.malgo.annotation.request.doc;

import cn.malgo.annotation.enums.OriginalDocState;
import lombok.Data;

import java.util.Set;

@Data
public class ListDocRequest {
  private int pageIndex;
  private int pageSize;
  private int minTextLength;
  private String name; // 文档名字
  private String source; // 文档来源
  private Set<OriginalDocState> docState;
  private String text; // 文档内容
  private String type; // 文档类型
}
