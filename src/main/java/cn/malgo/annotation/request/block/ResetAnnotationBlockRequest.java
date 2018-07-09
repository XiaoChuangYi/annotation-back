package cn.malgo.annotation.request.block;

import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import java.util.List;
import lombok.Value;

@Value
public class ResetAnnotationBlockRequest {
  private List<Integer> idList; // block状态重新打回标注和审核id
  private AnnotationBlockActionEnum action; // 操作
}
