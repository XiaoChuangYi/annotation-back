package com.microservice.enums;

/**
 * Created by cjl on 2018/5/17.
 */
public enum AnnotationWordPosExerciseStateEnum {

    INIT("待练习"),


    FINISH("完成练习");



    private String message;

    AnnotationWordPosExerciseStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
