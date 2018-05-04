package com.microservice.enums;

/**
 * Created by cjl on 2018/4/16.
 */
public enum AnnotationSentExercisesStateEnum {

    INIT("待练习"),


    FINISH("完成练习");



    private String message;

    AnnotationSentExercisesStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
