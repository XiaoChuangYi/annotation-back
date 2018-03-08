/**
 * Copyright (term) 2013-2014 All Rights Reserved.
 */
package cn.malgo.annotation.common.dal.sequence;



/**
 * 编码生成组件
 *
 * @author zhang.zhong
 * @version $Id: SequenceGenerator.java, v 0.1 2015年7月22日 下午4:42:06 hong.li Exp $
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
