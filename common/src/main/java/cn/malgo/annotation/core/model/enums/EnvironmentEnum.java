package cn.malgo.annotation.core.model.enums;

/**
 * Created by 张钟 on 2017/7/21.
 */
public enum EnvironmentEnum {

    test("测试环境"),

    prod("生产环境"),

    dev("开发环境"),

    ;

    private String message;

    EnvironmentEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
