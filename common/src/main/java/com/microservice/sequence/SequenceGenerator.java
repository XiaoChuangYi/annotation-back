package com.microservice.sequence;

import com.microservice.enums.CodeGenerateTypeEnum;

/**
 * Created by cjl on 2018/4/12.
 */
public interface SequenceGenerator {

    /**
     * 根据类型获得编码
     * @param codeType  编码类型枚举
     * @return
     */
    String nextCodeByType(CodeGenerateTypeEnum codeType);

    /**
     * 生成序列
     * @return
     */
    Long nextCodeByType();
}
