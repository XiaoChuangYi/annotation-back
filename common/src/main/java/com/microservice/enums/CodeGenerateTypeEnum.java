package com.microservice.enums;

/**
 * Created by cjl on 2018/4/12.
 */
public enum CodeGenerateTypeEnum {
    /** 用户前缀*/
    USER("1"),

    /** 默认使用7,luck number **/
    DEFAULT("7"),

    /** 日志前缀 **/
    LOG("9"),

    ;

    private String value;

    CodeGenerateTypeEnum(String value){
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}

