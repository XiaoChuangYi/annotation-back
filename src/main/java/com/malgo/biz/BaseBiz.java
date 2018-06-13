package com.malgo.biz;

import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

/** Created by cjl on 2018/5/30. */
@Slf4j
public abstract class BaseBiz<REQ, RES> {

  protected abstract void validateRequest(REQ req) throws InvalidInputException;

  protected abstract void authorize(int userId, int role, REQ req) throws BusinessRuleException;

  protected RES doBiz(int userId, int role, REQ req) {
    return this.doBiz(req);
  }

  protected RES doBiz(REQ req) {
    throw new NotImplementedException("do biz is not implemented in " + this.getClass());
  }

  private boolean isServerSideException(final Throwable ex) {
    return !(ex instanceof InvalidInputException) && !(ex instanceof BusinessRuleException);
  }

  public RES process(REQ req, int userId, int role) {
    // TODO add metrics
    log.info("start biz {}, {}", this.getClass(), req);

    try {
      validateRequest(req);
      authorize(userId, role, req);
      return doBiz(userId, role, req);
    } catch (Throwable ex) {
      if (isServerSideException(ex)) {
        log.error(ex.getMessage(), ex);
      } else {
        log.warn(ex.getMessage(), ex);
      }

      throw ex;
    } finally {
      // TODO record metric time
    }
  }
}
