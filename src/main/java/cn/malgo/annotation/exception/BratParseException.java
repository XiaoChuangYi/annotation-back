package cn.malgo.annotation.exception;

import org.springframework.http.HttpStatus;

/** Created by cjl on 2018/6/7. */
public class BratParseException extends MalgoServiceException {

  public BratParseException(String code, String message) {
    super(code, message);
  }

  public BratParseException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
