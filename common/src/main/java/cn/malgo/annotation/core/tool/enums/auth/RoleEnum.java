package cn.malgo.annotation.core.tool.enums.auth;

/**
 * Created by cjl on 2017/11/17.
 */
public enum RoleEnum {
    ADMIN("admin"),

    CONSUMER("consumer"),

    ;

    private String value;
    RoleEnum(String value){
        this.value=value;
    }
    public String getValue() {
        return value;
    }
}
