package cn.malgo.annotation.request.block;

import lombok.Data;

import java.util.List;

@Data
public class ResetAnnotationBlockRequest {
  // block状态重新打回标注和审核id
  private List<Long> ids;

  /** {@link cn.malgo.annotation.enums.AnnotationBlockActionEnum} */
  private String action;
}
