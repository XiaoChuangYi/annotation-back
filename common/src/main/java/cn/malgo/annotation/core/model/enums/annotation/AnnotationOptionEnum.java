package cn.malgo.annotation.core.model.enums.annotation;

/**
 * @author 张钟
 * @date 2017/10/25
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
