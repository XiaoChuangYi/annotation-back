package cn.malgo.annotation.core.tool.enums.annotation;

/**
 * Created by cjl on 2017/11/20.
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
