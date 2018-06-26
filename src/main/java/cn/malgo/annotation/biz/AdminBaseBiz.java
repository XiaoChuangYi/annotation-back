package cn.malgo.annotation.biz;

import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;

public abstract class AdminBaseBiz<REQ, RES> extends BaseBiz<REQ, RES> {
  @Override
  protected void authorize(int userId, int role, REQ req) throws BusinessRuleException {
    if (role != AnnotationRoleStateEnum.admin.getRole()) {
      throw new BusinessRuleException("permission-denied", "仅管理员可以操作");
    }
  }
}
