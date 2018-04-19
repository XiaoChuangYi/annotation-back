package com.microservice.enums;

/**
 * Created by cjl on 2018/4/11.
 */
public enum AnnotationOptionEnum {
    NEW_TERM("新词"),

    ANNOTATION("标注"),

    ;

    private String message;

    AnnotationOptionEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
