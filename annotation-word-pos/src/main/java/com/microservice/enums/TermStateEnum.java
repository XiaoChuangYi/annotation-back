package com.microservice.enums;

/**
 * Created by cjl on 2018/4/25.
 */
public enum TermStateEnum {
    INIT("待自动标注"),

    FINISH("已自动标注");

    private String message;

    TermStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
