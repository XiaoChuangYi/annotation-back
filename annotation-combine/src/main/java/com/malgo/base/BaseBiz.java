package com.malgo.base;

import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by cjl on 2018/5/30.
 */
@Slf4j
public abstract class BaseBiz<REQ,RES> {

  protected abstract void validateRequest(REQ req) throws InvalidInputException;
  protected abstract void authorize(String authToken,REQ req) throws BusinessRuleException;
  protected abstract RES doBiz(REQ req);

  private boolean isServerSideException(final Throwable ex) {
    return !(ex instanceof InvalidInputException) && !(ex instanceof BusinessRuleException);
  }

  public RES process(REQ req, String authToken) {
    // TODO add metrics
    log.info("start biz {}, {}", this.getClass(), req);

    try {
      validateRequest(req);
      authorize(authToken, req);
      return doBiz(req);
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
