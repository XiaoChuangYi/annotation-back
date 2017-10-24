package cn.malgo.annotation.web.controller.rule.request;

import cn.malgo.annotation.common.util.AssertUtil;

/**
 * @author 张钟
 * @date 2017/10/24
 */
public class RuleUpdateRequest {

    private String ruleId;

    private String ruleName;

    private String ruleValue;

    private String ruleType;

    private String memo;

    public static void check(RuleUpdateRequest ruleAddRequest){
        AssertUtil.notNull(ruleAddRequest,"新增规则请求对象为空");
        AssertUtil.notBlank(ruleAddRequest.getRuleName(),"规则名称为空");
        AssertUtil.notBlank(ruleAddRequest.getRuleId(),"规则ID为空");
        AssertUtil.notBlank(ruleAddRequest.getRuleValue(),"规则内容为空");
        AssertUtil.notBlank(ruleAddRequest.getRuleType(),"规则类型为空");
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

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}
