package cn.malgo.annotation.web.controller.rule.request;

import cn.malgo.annotation.web.request.PageRequest;

/**
 * @author 张钟
 * @date 2017/10/24
 */
public class RulePageQueryRequest extends PageRequest {

    /** 规则名称 **/
    private String ruleName;

    /** 规则内容 **/
    private String ruleValue;

    /** 规则类型 **/
    private String ruleType;

    /** 规则状态 **/
    private String state;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
