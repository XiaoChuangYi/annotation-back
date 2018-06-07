package com.malgo.exception;

import com.malgo.result.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by cjl on 2018/5/28.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle extends ResponseEntityExceptionHandler {
    private static final String EXCEPTION_MESSAGE = "ExceptionCode = ";

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        if (ex instanceof MalgoServiceException) {
            if (((MalgoServiceException) ex).getStatus().is5xxServerError()) {
                log.error(EXCEPTION_MESSAGE + HttpStatus.INTERNAL_SERVER_ERROR, ex);
            } else {
                log.warn(EXCEPTION_MESSAGE + ((MalgoServiceException) ex).getStatus(), ex);
            }

            return new ResponseEntity<>(
                new Response<>(((MalgoServiceException) ex).getCode(), ex.getMessage()),
                ((MalgoServiceException) ex).getStatus());
        }

        log.error(EXCEPTION_MESSAGE + HttpStatus.INTERNAL_SERVER_ERROR, ex);
        return new ResponseEntity<>(
            new Response<>("unknown-error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
