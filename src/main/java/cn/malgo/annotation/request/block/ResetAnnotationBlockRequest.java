package cn.malgo.annotation.request.block;

import lombok.Value;

import java.util.List;

@Value
public class ResetAnnotationBlockRequest {
  // block状态重新打回标注和审核id
  private List<Long> ids;

  /** {@link cn.malgo.annotation.enums.AnnotationBlockActionEnum} */
  private String action;
}
