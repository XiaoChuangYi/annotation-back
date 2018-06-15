package com.malgo.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by cjl on 2018/5/30.
 */
public class BusinessRuleException extends MalgoServiceException {


  public BusinessRuleException(String code, String message) {
    super(code, message);
  }

  public BusinessRuleException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

  @Override
  public HttpStatus getStatus() {
    //待定，抛出这个错误在业务逻辑层感觉不合理
    return HttpStatus.FORBIDDEN;
  }
}
