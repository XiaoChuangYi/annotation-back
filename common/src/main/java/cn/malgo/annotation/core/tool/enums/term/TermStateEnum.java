package cn.malgo.annotation.core.tool.enums.term;

/**
 * Created by 张钟 on 2017/10/18.
 */
public enum TermStateEnum {

    INIT("待自动标注"),

    FINISH("已自动标注");

    private String message;

    TermStateEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
