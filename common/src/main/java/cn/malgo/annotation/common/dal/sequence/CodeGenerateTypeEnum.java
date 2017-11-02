package cn.malgo.annotation.common.dal.sequence;

/**
 * Created by ZhangZhong on 2017/5/6.
 */



/**
 *  序列号生成器类型
 *
 * @author hong.li
 * @version $Id: CodeGenerateTypeEnum.java, v 0.1 2015年7月22日 下午4:43:22 hong.li Exp $
 */
public enum CodeGenerateTypeEnum {

    /** 用户前缀*/
    USER("1"),

    /** 默认使用7,luck number **/
    DEFAULT("7"),

    /** 日志前缀 **/
    LOG("9"),

    ;

    private String value;

    CodeGenerateTypeEnum(String value){
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}
