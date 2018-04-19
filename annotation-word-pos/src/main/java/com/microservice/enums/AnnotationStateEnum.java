package com.microservice.enums;

/**
 * Created by cjl on 2018/4/12.
 */
public enum AnnotationStateEnum {
    INIT("待人工标注"),

    PROCESSING("标注中"),

    UN_RECOGNIZE("无法识别"),

    FINISH("标注完成"),

    INCONSISTENT("不一致"),

    UN_ENCRYPTED("未加密");

    private String message;

    AnnotationStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
