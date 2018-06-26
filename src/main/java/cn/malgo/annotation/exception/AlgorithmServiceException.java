package cn.malgo.annotation.exception;

import org.springframework.http.HttpStatus;

/** Created by cjl on 2018/5/31. */
public class AlgorithmServiceException extends MalgoServiceException {

  public AlgorithmServiceException(String code, String message) {
    super(code, message);
  }

  public AlgorithmServiceException(String code, String message, Throwable cause) {
    super(code, message, cause);
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.SERVICE_UNAVAILABLE;
  }
}
