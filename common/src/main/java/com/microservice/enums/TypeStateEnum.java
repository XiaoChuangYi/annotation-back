package com.microservice.enums;

/**
 * Created by cjl on 2018/4/12.
 */
public enum TypeStateEnum {
    ENABLE("启用"),

    DISABLE("停用"),
    ;

    private String message;

    TypeStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
