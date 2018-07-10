package cn.malgo.annotation.biz.base;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.dto.UserDetails;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Arrays;

@Slf4j
public abstract class BaseBiz<REQ, RES> {
  protected abstract void validateRequest(REQ req) throws InvalidInputException;

  protected void authorize(int userId, int role, REQ req) throws BusinessRuleException {}

  private void checkRole(int role) {
    final RequireRole requireRole = getClass().getAnnotation(RequireRole.class);

    if (requireRole == null || requireRole.value().length == 0) {
      return;
    }

    final AnnotationRoleStateEnum roleEnum = AnnotationRoleStateEnum.valueOf(role);
    if (roleEnum == null) {
      throw new InvalidInputException("invalid-role", "invalid role: " + role);
    }

    if (Arrays.asList(requireRole.value()).indexOf(roleEnum) == -1) {
      throw new BusinessRuleException("permission-denied", roleEnum.name() + "无权限");
    }
  }

  protected RES doBiz(int userId, int role, REQ req) {
    return this.doBiz(req);
  }

  protected RES doBiz(REQ req) {
    throw new NotImplementedException("do biz is not implemented in " + this.getClass());
  }

  private boolean isServerSideException(final Throwable ex) {
    return !(ex instanceof InvalidInputException) && !(ex instanceof BusinessRuleException);
  }

  public RES process(REQ req, UserDetails user) {
    return process(req, user.getId(), user.getRoleId());
  }

  public RES process(REQ req, int userId, int role) {
    // TODO add metrics
    log.info("start biz {}, {}", this.getClass(), req);

    try {
      validateRequest(req);
      checkRole(role);
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
