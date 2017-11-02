package cn.malgo.annotation.core.model.enums;

/**
 * @author 张钟
 * @date 2017/11/2
 */
public enum ProcessStateEnum {

    INIT("待处理"),

    PROCESSING("处理中"),

    FINISH("处理完成"),

    ;
    private String message;

    ProcessStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
