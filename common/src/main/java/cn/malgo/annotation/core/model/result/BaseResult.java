package cn.malgo.annotation.core.model.result;

import java.io.Serializable;

import cn.malgo.annotation.core.model.enums.EnumBase;
import cn.malgo.annotation.core.model.enums.BaseResultCodeEnum;

public class BaseResult implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = -313965489774631737L;

    /** 成功失败标识 (悲观模式)*/
    protected boolean         success          = false;

    /** 结果码 */
    protected String          code             = BaseResultCodeEnum.SYSTEM_ERROR.getCode();

    /** 返回信息 */
    protected String          message          = BaseResultCodeEnum.SYSTEM_ERROR.getMessage();

    /**
     * 构造方法
     * @param success       成功标识
     */
    public BaseResult(boolean success) {
        if (success) {
            this.success = true;
            this.code = BaseResultCodeEnum.SUCCESS.getCode();
            this.message = BaseResultCodeEnum.SUCCESS.getMessage();
        }
    }

    public BaseResult(boolean success,String message) {
        if (success) {
            this.success = true;
            this.code = BaseResultCodeEnum.SUCCESS.getCode();
            this.message = BaseResultCodeEnum.SUCCESS.getMessage();
        }else{
            this.message = message;
        }
    }

    /**
     * 设置结果集
     *
     * @param success
     * @param resultCode
     */
    public void markResult(boolean success, EnumBase resultCode) {
        this.success = success;
        if (resultCode != null) {
            this.code = resultCode.name();
            this.message = resultCode.message();
        }
    }

    /**
     * 设置指定异常枚举CODE + 自定义异常码
     *
     * @param success
     * @param resultCode
     */
    public void markResult(boolean success, EnumBase resultCode, String message) {
        this.success = success;
        if (resultCode != null) {
            this.code = resultCode.name();
            this.message = message;
        }
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
     * Setter method for property <tt>code</tt>.
     *
     * @param code value to be assigned to property code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Getter method for property <tt>message</tt>.
     *
     * @return property value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter method for property <tt>message</tt>.
     *
     * @param message value to be assigned to property message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter method for property <tt>success</tt>.
     *
     * @return property value of success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter method for property <tt>success</tt>.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

}
