package cn.malgo.annotation.common.util;

/**
 * yingyinglicai.com Inc.
 * Copyright (term) 2013-2013 All Rights Reserved.
 */

import java.util.Collection;
import java.util.Map;

import cn.malgo.annotation.core.tool.enums.BaseResultCodeEnum;
import cn.malgo.annotation.core.tool.enums.EnumBase;
import cn.malgo.annotation.common.util.exception.BaseRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * <pre> 数据校验组件，封装常用的数据校验，返回业务异常
 *      仅在biz层中对参数进行基本校验
 * </pre>
 *
 * @author qiang.wq
 * @version $Id: AssertUtil.java, v 0.1 2013-11-15 下午7:47:22 WJL Exp $
 */
public class AssertUtil {

    /**
     * 判断对象不为空
     *
     * @param object 对象
     * @exception BaseRuntimeException 如果对象为空
     */
    public static void notNull(Object object) {
        if (object == null) {
            throw new BaseRuntimeException(BaseResultCodeEnum.NULL_ARGUMENT);
        }
    }

    /**
     * 判断对象不为空
     *
     * @param object  对象
     * @param message 错误信息
     * @exception BaseRuntimeException 如果对象为空
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BaseRuntimeException(BaseResultCodeEnum.NULL_ARGUMENT, message);
        }
    }

    /**
     * 判断一个集合不为空
     *
     * @param collection 集合
     * @exception BaseRuntimeException 如果集合为空
     */
    @SuppressWarnings({ "rawtypes" })
    public static void notEmpty(Collection collection) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BaseRuntimeException(BaseResultCodeEnum.NULL_ARGUMENT);
        }
    }

    /**
     * 判断一个集合不为空
     *
     * @param collection 集合
     * @param message 错误信息
     * @exception BaseRuntimeException 如果集合为空
     */
    @SuppressWarnings({ "rawtypes" })
    public static void notEmpty(Collection collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new BaseRuntimeException(BaseResultCodeEnum.NULL_ARGUMENT, message);
        }
    }

    /**
     * 判断一个Map不为空
     *
     * @param map 映射
     * @exception BaseRuntimeException 如果Map为空
     */
    @SuppressWarnings("rawtypes")
    public static void notEmpty(Map map) {
        if (CollectionUtils.isEmpty(map)) {
            throw new BaseRuntimeException(BaseResultCodeEnum.NULL_ARGUMENT);
        }
    }

    /**
     * 判断一个Map不为空
     *
     * @param map 映射
     * @param message 错误信息
     * @exception BaseRuntimeException 如果Map为空
     */
    @SuppressWarnings("rawtypes")
    public static void notEmpty(Map map, String message) {
        if (CollectionUtils.isEmpty(map)) {
            throw new BaseRuntimeException(BaseResultCodeEnum.NULL_ARGUMENT, message);
        }
    }

    /**
     * 判断一个字符串不为空
     *
     * @param text 字符串
     * @exception BaseRuntimeException 如果字符串为空
     */
    public static void notBlank(String text) {
        if (StringUtils.isBlank(text)) {
            throw new BaseRuntimeException(BaseResultCodeEnum.NULL_ARGUMENT);
        }
    }

    /**
     * 判断一个字符串不为空
     *
     * @param text 字符串
     * @param message 错误信息
     * @exception BaseRuntimeException 如果字符串为空
     */
    public static void notBlank(String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new BaseRuntimeException(BaseResultCodeEnum.NULL_ARGUMENT, message);
        }
    }

    /**
     * 判断一个对象是某个类的实例
     *
     * @param type 类
     * @param obj 实例
     * @exception BaseRuntimeException 如果对象不是该类的实例
     */
    @SuppressWarnings({ "rawtypes" })
    public static void isInstanceOf(Class type, Object obj) {
        notNull(type);
        if (!type.isInstance(obj)) {
            throw new BaseRuntimeException(BaseResultCodeEnum.ILLEGAL_ARGUMENT);
        }
    }

    /**
     * 判断一个对象是某个类的实例
     *
     * @param type 类
     * @param obj 实例
     * @param message 错误信息
     * @exception BaseRuntimeException 如果对象不是该类的实例
     */
    @SuppressWarnings("rawtypes")
    public static void isInstanceOf(Class type, Object obj, String message) {
        notNull(type);
        if (!type.isInstance(obj)) {
            throw new BaseRuntimeException(BaseResultCodeEnum.ILLEGAL_ARGUMENT, message);
        }
    }

    /**
     * 判断一个表达式是否成立,不成立则抛参数异常
     *
     * @param expression            布尔表达式
     * @exception BaseRuntimeException      如果表达式不成立
     */
    public static void state(boolean expression) {
        if (!expression) {
            throw new BaseRuntimeException(BaseResultCodeEnum.ILLEGAL_ARGUMENT);
        }
    }

    /**
     * 判断一个表达式是否成立,不成立则抛参数异常加异常信息
     *
     * @param expression    布尔表达式
     * @param message       错误信息
     * @exception BaseRuntimeException 如果表达式不成立
     */
    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new BaseRuntimeException(BaseResultCodeEnum.ILLEGAL_ARGUMENT, message);
        }
    }

    public static void state(boolean expression,BaseResultCodeEnum baseResultCodeEnum) {
        if (!expression) {
            throw new BaseRuntimeException(baseResultCodeEnum, baseResultCodeEnum.getMessage());
        }
    }

    public static void state(boolean expression,EnumBase enumBase) {
        if (!expression) {
            throw new BaseRuntimeException(enumBase);
        }
    }
}
