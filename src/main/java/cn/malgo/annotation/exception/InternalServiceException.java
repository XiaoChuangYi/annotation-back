package cn.malgo.annotation.exception;

import org.springframework.http.HttpStatus;

/** Created by cjl on 2018/5/28. */
public class InternalServiceException extends MalgoServiceException {
  public InternalServiceException(String code, String message) {
    super(code, message);
  }

  public InternalServiceException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
