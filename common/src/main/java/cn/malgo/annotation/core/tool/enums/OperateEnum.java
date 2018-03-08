package cn.malgo.annotation.core.tool.enums;

/**
 * @author 张钟
 * @date 2017/11/2
 */
public enum OperateEnum {


    ADD("新增"),

    UPDATE("更新"),

    DELETE("删除"),

    ;
    private String message;

    OperateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
