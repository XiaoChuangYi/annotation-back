package cn.malgo.annotation.web.controller.rule.request;

import com.alibaba.fastjson.JSON;

import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.common.util.exception.BaseRuntimeException;
import cn.malgo.annotation.core.model.enums.BaseResultCodeEnum;
import cn.malgo.annotation.core.model.enums.rule.RuleEnum;
import cn.malgo.core.definition.EntityRule;

/**
 * @author 张钟
 * @date 2017/10/24
 */
public class RuleAddRequest {

    private String ruleName;

    private String ruleValue;

    private String ruleType;

    private String memo;

    public static void check(RuleAddRequest ruleAddRequest) {
        AssertUtil.notNull(ruleAddRequest, "新增规则请求对象为空");
        AssertUtil.notBlank(ruleAddRequest.getRuleName(), "规则名称为空");
        AssertUtil.notBlank(ruleAddRequest.getRuleValue(), "规则内容为空");
        AssertUtil.notBlank(ruleAddRequest.getRuleType(), "规则类型为空");
        try {
            RuleEnum.valueOf(ruleAddRequest.getRuleType());
        } catch (Exception e) {
            throw new BaseRuntimeException(BaseResultCodeEnum.ILLEGAL_ARGUMENT, "规则类型错误");
        }

        try {
            JSON.parseObject(ruleAddRequest.getRuleValue()).toJavaObject(EntityRule.class);
        } catch (Exception e) {
            throw new BaseRuntimeException(BaseResultCodeEnum.ILLEGAL_ARGUMENT, "规则格式错误");
        }

    }

    public String getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
