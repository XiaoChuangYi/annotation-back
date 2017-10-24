package cn.malgo.annotation.common.dal.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "RL_ENTITY_RULE")
public class RlEntityRule {
    /**
     * 主键,规则ID
     */
    @Id
    @Column(name = "RULE_ID")
    private String ruleId;

    /**
     * 规则名称
     */
    @Column(name = "RULE_NAME")
    private String ruleName;

    /**
     * 规则类型
     */
    @Column(name = "RULE_TYPE")
    private String ruleType;

    /**
     * 状态
     */
    @Column(name = "STATE")
    private String state;

    /**
     * 备注
     */
    @Column(name = "MEMO")
    private String memo;

    /**
     * 创建时间
     */
    @Column(name = "GMT_CREATED")
    private Date gmtCreated;

    /**
     * 修改时间
     */
    @Column(name = "GMT_MODIFIED")
    private Date gmtModified;

    /**
     * 规则内容
     */
    @Column(name = "RULE_VALUE")
    private String ruleValue;

    /**
     * 获取主键,规则ID
     *
     * @return RULE_ID - 主键,规则ID
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * 设置主键,规则ID
     *
     * @param ruleId 主键,规则ID
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * 获取规则类型
     *
     * @return RULE_TYPE - 规则类型
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * 设置规则类型
     *
     * @param ruleType 规则类型
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * 获取状态
     *
     * @return STATE - 状态
     */
    public String getState() {
        return state;
    }

    /**
     * 设置状态
     *
     * @param state 状态
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 获取备注
     *
     * @return MEMO - 备注
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 设置备注
     *
     * @param memo 备注
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }

    /**
     * 获取创建时间
     *
     * @return GMT_CREATED - 创建时间
     */
    public Date getGmtCreated() {
        return gmtCreated;
    }

    /**
     * 设置创建时间
     *
     * @param gmtCreated 创建时间
     */
    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    /**
     * 获取修改时间
     *
     * @return GMT_MODIFIED - 修改时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 设置修改时间
     *
     * @param gmtModified 修改时间
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * 获取规则内容
     *
     * @return RULE_VALUE - 规则内容
     */
    public String getRuleValue() {
        return ruleValue;
    }

    /**
     * 设置规则内容
     *
     * @param ruleValue 规则内容
     */
    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}