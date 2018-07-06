package cn.malgo.annotation.request.doc;

import cn.malgo.annotation.enums.OriginalDocState;
import java.util.Set;
import lombok.Value;

@Value
public class ListDocRequest {
  int pageIndex;
  int pageSize;
  String name; // 文档名字
  String source; // 文档来源
  Set<OriginalDocState> docState;
  String text; // 文档内容
  String type; // 文档类型
}
