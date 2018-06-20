package com.malgo.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by cjl on 2018/5/28.
 */
public class InvalidInputException extends MalgoServiceException {


  public InvalidInputException(String code, String message) {
    super(code, message);
  }

  public InvalidInputException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.BAD_REQUEST;
  }
}