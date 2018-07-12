package cn.malgo.annotation.request;

import cn.malgo.annotation.dto.error.AnnotationErrorContext;
import cn.malgo.annotation.dto.error.FixAnnotationEntity;
import cn.malgo.annotation.dto.error.FixAnnotationErrorData;
import cn.malgo.annotation.dto.error.FixAnnotationRelationEntity;
import lombok.Data;

import java.util.List;

@Data
public class FixAnnotationErrorRequest implements FixAnnotationErrorData {
  private int errorType;
  // 需要修复的标注block
  private List<AnnotationErrorContext> annotations;
  // 分词、实体数据
  private List<FixAnnotationEntity> entities;
  // 关联数据
  private List<FixAnnotationRelationEntity> relations;
  // 关联对外实体index
  private int activeEntity = -1;
  // 非法关联修复
  private IllegalRelationRepairRequest illegalRelationRepair;

  @Data
  public static class IllegalRelationRepairRequest implements IllegalRelationRepairData {
    private boolean reverse;
    private String type;
  }
}
