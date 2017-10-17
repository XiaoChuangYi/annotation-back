package cn.malgo.annotation.core.model.enums;


public enum BaseResultCodeEnum implements EnumBase {

    /** 操作成功 */
    SUCCESS("SUCCESS", "操作成功"),

    /** 系统异常*/
    SYSTEM_ERROR("SYSTEM_ERROR", "系统异常，请联系管理员！"),

    /** 外部接口调用异常*/
    INTERFACE_SYSTEM_ERROR("INTERFACE_SYSTEM_ERROR", "外部接口调用异常，请联系管理员！"),

    /** 业务连接处理超时 */
    CONNECT_TIME_OUT("CONNECT_TIME_OUT", "系统超时，请稍后再试!"),

    /** 系统错误 */
    SYSTEM_FAILURE("SYSTEM_FAILURE", "系统错误"),

    /** 参数为空 */
    NULL_ARGUMENT("NULL_ARGUMENT", "参数为空"),

    /** 参数不正确 */
    ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", "参数不正确"),

    /** 逻辑错误 */
    LOGIC_ERROR("LOGIC_ERROR", "逻辑错误"),

    /** 无操作权限 */
    NO_OPERATE_PERMISSION("NO_OPERATE_PERMISSION", "无操作权限"),

    /** 数据异常 */
    DATA_ERROR("DATA_ERROR", "数据异常"),

    /** 交易金额不正确 */
    TRADE_MONEY_ERROR("TRADE_MONEY_ERROR", "交易金额不正确"),


    LOGIN_VALIDATE_CODE_ERROR("LOGIN_VALIDATE_CODE_ERROR","登录验证码错误"),

    ;

    /** 枚举编号 */
    private String code;

    /** 枚举详情 */
    private String message;

    /**
     * 构造方法
     *
     * @param code         枚举编号
     * @param message      枚举详情
     */
    private BaseResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过枚举<code>code</code>获得枚举。
     *
     * @param code         枚举编号
     * @return
     */
    public static BaseResultCodeEnum getResultCodeEnumByCode(String code) {
        for (BaseResultCodeEnum param : values()) {
            if (param.getCode().equals(code)) {
                return param;
            }
        }
        return null;
    }

    /**
     * Getter method for property <tt>code</tt>.
     *
     * @return property value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Getter method for property <tt>message</tt>.
     *
     * @return property value of message
     */
    public String getMessage() {
        return message;
    }


    @Override
    public String message() {
        return message;
    }


    @Override
    public Number value() {
        return null;
    }
}
