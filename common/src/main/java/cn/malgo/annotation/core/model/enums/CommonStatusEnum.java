package cn.malgo.annotation.core.model.enums;

/**
 * Created by 张钟 on 2017/7/1.
 */
public enum CommonStatusEnum {

                              ENABLE("可用"),

                              UN_ENABLE("不可用"),

    ;

    private String message;

    CommonStatusEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
