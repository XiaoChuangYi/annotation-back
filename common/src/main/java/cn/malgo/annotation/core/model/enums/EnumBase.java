package cn.malgo.annotation.core.model.enums;

/**
 * 枚举基类
 *
 */
public interface EnumBase {

    /**
     * 获取枚举名(建议与enumCode保持一致)
     *
     * @return
     */
    String name();

    /**
     * 获取枚举消息
     *
     * @return
     */
    String message();

    /**
     * 获取枚举值
     *
     * @return
     */
    Number value();
}


