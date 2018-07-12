package cn.malgo.annotation.biz.base;

import cn.malgo.annotation.dto.UserDetails;
import org.springframework.transaction.annotation.Transactional;

public abstract class TransactionalBiz<REQ, RES> extends BaseBiz<REQ, RES> {
  @Override
  @Transactional
  public RES process(final REQ req, final UserDetails user) {
    return super.process(req, user);
  }

  @Override
  @Transactional
  public RES process(final REQ req, final int userId, final int role) {
    return super.process(req, userId, role);
  }
}
