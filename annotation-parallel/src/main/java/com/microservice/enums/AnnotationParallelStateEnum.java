package com.microservice.enums;

/**
 * Created by cjl on 2018/4/16.
 */
public enum AnnotationParallelStateEnum {

    UN_DISTRIBUTION("未分配"),

    DISTRIBUTIONED("已分配"),

    ANNOTATIONING("标注中"),

    FINISH("已标注"),

    EXAMINED("已审核");


    private String message;

    AnnotationParallelStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
