/**
 * yingyinglicai.com Inc.
 * Copyright (term) 2013-2013 All Rights Reserved.
 */
package cn.malgo.annotation.common.util.exception;

import cn.malgo.annotation.core.tool.enums.EnumBase;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * 运行时异常
 *
 * @author qiang.wq
 * @version $Id: BaseRuntimeException.java, v 0.1 2013-10-30 下午3:10:35 WJL Exp $
 */
public class BaseRuntimeException extends RuntimeException {

    /**  serialVersionUID */
    private static final long serialVersionUID = 8321149154706648074L;

    protected String          code;

    protected String          message;

    // 异常枚举
    protected EnumBase errorEnum;

    /**
     * 空构造器。
     */
    public BaseRuntimeException() {
        super();
    }

    /**
     * 构造器。
     *
     * @param message
     *            消息
     */
    public BaseRuntimeException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * 构造器。
     *
     * @param baseEnum
     *            消息
     */
    public BaseRuntimeException(EnumBase baseEnum) {
        super(baseEnum.message());
        this.code = baseEnum.name();
        this.message = baseEnum.message();
        this.errorEnum = baseEnum;
    }

    /**
     * 构造器。
     *
     * @param message
     *            消息
     */
    public BaseRuntimeException(EnumBase baseEnum, String message) {
        super(message);
        this.code = baseEnum.name();
        this.errorEnum = baseEnum;
        this.message = message;
    }

    /**
     * 构造器。
     *
     * @param message
     *            消息
     */
    public BaseRuntimeException(String errorCode, String message) {
        super(message);
        this.code = errorCode;
        this.message = message;
    }

    /**
     * 构造器。
     *
     * @param cause
     *            原因
     */
    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造器。
     *
     * @param message
     *            消息
     * @param cause
     *            原因
     */
    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造器。
     *
     * @param message
     *            消息
     * @param cause
     *            原因
     */
    public BaseRuntimeException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode;
        this.message = message;
    }

    /**
     * @see Throwable#toString()
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Getter method for property <tt>errorEnum</tt>.
     *
     * @return property value of errorEnum
     */
    public EnumBase getErrorEnum() {
        return errorEnum;
    }
}
