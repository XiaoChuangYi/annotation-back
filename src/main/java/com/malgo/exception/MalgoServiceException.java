package com.malgo.exception;

import org.springframework.http.HttpStatus;

/**
 * Created by cjl on 2018/5/28.
 */
public abstract class MalgoServiceException extends RuntimeException {

    private final String code;

    public MalgoServiceException(String code,String message){
        super(message);
        this.code=code;
    }

    public MalgoServiceException(String code,String message,Throwable cause){
       super(message,cause);
       this.code=code;
    }
    public String getCode() {
        return this.code;
    }

    public abstract HttpStatus getStatus();
}
