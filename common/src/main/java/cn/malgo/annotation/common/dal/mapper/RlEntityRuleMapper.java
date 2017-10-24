package cn.malgo.annotation.common.dal.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.malgo.annotation.common.dal.model.RlEntityRule;
import cn.malgo.annotation.common.dal.util.CommonMapper;

public interface RlEntityRuleMapper extends CommonMapper<RlEntityRule> {

    /**
     * 通过规则内容查询规则
     * @param ruleValue
     * @param ruleType
     * @return
     */
    RlEntityRule selectByRule(@Param("ruleValue") String ruleValue,
                              @Param("ruleType") String ruleType);

    /**
     * 多条件查询,主要用于规则检索
     * @param ruleName 规则名称,支持模糊查询 %x%
     * @param ruleValue 规则内容,支持模糊查询 %x%
     * @param ruleType 规则类型
     * @param state 规则状态
     * @return
     */
    List<RlEntityRule> selectByOnePage(@Param("ruleName") String ruleName,
                                       @Param("ruleValue") String ruleValue,
                                       @Param("ruleType") String ruleType,
                                       @Param("state") String state);

}