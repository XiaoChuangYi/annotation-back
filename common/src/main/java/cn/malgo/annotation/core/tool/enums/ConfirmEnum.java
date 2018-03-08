package cn.malgo.annotation.core.tool.enums;

/**
 * Created by 张钟 on 2017/7/20.
 */
public enum ConfirmEnum {

    YES("是"),

    NO("否"),

    ;

    private String message;

    ConfirmEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
