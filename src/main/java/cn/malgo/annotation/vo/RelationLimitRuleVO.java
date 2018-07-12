package cn.malgo.annotation.vo;

import cn.malgo.annotation.entity.RelationLimitRule;
import java.util.List;
import lombok.Value;

@Value
public class RelationLimitRuleVO {
  private final List<RelationLimitRule> relationLimitRules;
}
