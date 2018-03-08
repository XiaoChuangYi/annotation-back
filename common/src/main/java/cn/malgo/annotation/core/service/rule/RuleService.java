package cn.malgo.annotation.core.service.rule;

import java.util.Date;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.malgo.annotation.common.dal.mapper.RlEntityRuleMapper;
import cn.malgo.annotation.common.dal.model.RlEntityRule;
import cn.malgo.annotation.common.dal.sequence.CodeGenerateTypeEnum;
import cn.malgo.annotation.common.dal.sequence.SequenceGenerator;
import cn.malgo.annotation.common.util.AssertUtil;
import cn.malgo.annotation.core.tool.enums.CommonStatusEnum;

/**
 *
 * @author 张钟
 * @date 2017/10/24
 */

@Service
public class RuleService {

    @Autowired
    private RlEntityRuleMapper rlEntityRuleMapper;

    @Autowired
    private SequenceGenerator  sequenceGenerator;

    /**
     * 保存rule
     * @param ruleName 规则名称
     * @param ruleValue 规则内容
     * @param ruleType 规则类型
     * @param memo 备注
     */
    public void saveRule(String ruleName, String ruleValue, String ruleType, String memo) {

        RlEntityRule rlEntityRule = rlEntityRuleMapper.selectByRule(ruleValue, ruleType);
        if (rlEntityRule != null) {
            return;
        }

        String rule_id = sequenceGenerator.nextCodeByType(CodeGenerateTypeEnum.DEFAULT);
        RlEntityRule rlEntityRuleNew = new RlEntityRule();
        rlEntityRuleNew.setRuleId(rule_id);
        rlEntityRuleNew.setRuleName(ruleName);
        rlEntityRuleNew.setRuleType(ruleType);
        rlEntityRuleNew.setRuleValue(ruleValue);
        rlEntityRuleNew.setState(CommonStatusEnum.ENABLE.name());
        rlEntityRuleNew.setMemo(memo);

        int insertResult = rlEntityRuleMapper.insert(rlEntityRuleNew);

        AssertUtil.state(insertResult > 0, "保存规则失败");

    }

    /**
     * 修改规则内容和类型
     * @param ruleId 规则ID
     * @param ruleName 规则名称
     * @param ruleValue 规则内容
     * @param ruleType 规则类型
     */
    public void modifyRuleValue(String ruleId, String ruleName, String ruleValue, String ruleType,
                                String memo) {

        //检查相同的规则是否已经存在,如果存在是否是当前规则
        RlEntityRule rlEntityRuleOld =  rlEntityRuleMapper.selectByRule(ruleValue,ruleType);
        if(rlEntityRuleOld!=null){
            AssertUtil.state(StringUtils.equals(ruleId,rlEntityRuleOld.getRuleId()),"规则已经存在");
        }

        RlEntityRule rlEntityRule = new RlEntityRule();
        rlEntityRule.setRuleId(ruleId);
        rlEntityRule.setRuleName(ruleName);
        rlEntityRule.setRuleValue(ruleValue);
        rlEntityRule.setRuleType(ruleType);
        rlEntityRule.setMemo(memo);
        rlEntityRule.setGmtModified(new Date());

        int updateResult = rlEntityRuleMapper.updateByPrimaryKeySelective(rlEntityRule);

        AssertUtil.state(updateResult > 0, "更新规则失败");
    }

    /**
     * 分页查询规则信息
     * @param ruleName
     * @param ruleValue
     * @param ruleType
     * @param state
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<RlEntityRule> queryOnePage(String ruleName, String ruleValue, String ruleType,
                                           String state, int pageNum, int pageSize) {
        Page<RlEntityRule> pageInfo = PageHelper.startPage(pageNum, pageSize);
        rlEntityRuleMapper.selectByOnePage(ruleName,ruleValue,ruleType,state);
        return pageInfo;
    }
}
