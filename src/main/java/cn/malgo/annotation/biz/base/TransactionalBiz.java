package cn.malgo.annotation.biz.base;

import cn.malgo.annotation.entity.UserAccount;
import org.springframework.transaction.annotation.Transactional;

public abstract class TransactionalBiz<REQ, RES> extends BaseBiz<REQ, RES> {
  @Override
  @Transactional
  public RES process(final REQ req, final UserAccount user) {
    return super.process(req, user);
  }

  @Override
  @Transactional
  public RES process(final REQ req, final int userId, final int role) {
    return super.process(req, userId, role);
  }
}
