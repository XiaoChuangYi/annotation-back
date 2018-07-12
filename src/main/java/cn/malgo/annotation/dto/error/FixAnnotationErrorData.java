package cn.malgo.annotation.dto.error;

import java.util.List;

public interface FixAnnotationErrorData {
  // 分词、实体数据
  List<FixAnnotationEntity> getEntities();
  // 关联数据
  List<FixAnnotationRelationEntity> getRelations();
  // 关联对外实体index
  int getActiveEntity();
  // 非法关联修复
  IllegalRelationRepairData getIllegalRelationRepair();

  interface IllegalRelationRepairData {
    String getType();

    boolean isReverse();
  }
}
