package cn.malgo.annotation.core.model.enums.annotation;

/**
 * Created by 张钟 on 2017/10/18.
 */
public enum AnnotationStateEnum {

    INIT("待人工标注"),

    PROCESSING("标注中"),

    FINISH("标注完成");

    private String message;

    AnnotationStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
