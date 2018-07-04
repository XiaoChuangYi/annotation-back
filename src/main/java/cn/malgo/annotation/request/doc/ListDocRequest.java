package cn.malgo.annotation.request.doc;

import java.util.List;
import lombok.Value;

@Value
public class ListDocRequest {
  private int pageIndex;
  private int pageSize;
  private String name; // 文档名字
  private String source; // 文档来源
  private List<String> docState;
  private String text; // 文档内容
  private String type; // 文档类型
}
