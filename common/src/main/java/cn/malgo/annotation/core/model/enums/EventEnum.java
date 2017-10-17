package cn.malgo.annotation.core.model.enums;

/**
 * Created by 张钟 on 2017/9/13.
 */
public enum EventEnum {


    TRADE("交易"),

    ;
    private String message;

    EventEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
