package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.entity.RelationLimitRule;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.vo.RelationLimitRuleVO;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
public class ListRelationLimitRuleBiz extends BaseBiz<BaseAnnotationRequest, RelationLimitRuleVO> {

  private final RelationLimitRuleRepository relationLimitRuleRepository;

  public ListRelationLimitRuleBiz(RelationLimitRuleRepository relationLimitRuleRepository) {
    this.relationLimitRuleRepository = relationLimitRuleRepository;
  }

  @Override
  protected void validateRequest(BaseAnnotationRequest baseAnnotationRequest)
      throws InvalidInputException {}

  @Override
  protected RelationLimitRuleVO doBiz(BaseAnnotationRequest baseAnnotationRequest) {
    final List<RelationLimitRule> relationLimitRules =
        relationLimitRuleRepository.findAll(Sort.by(Direction.ASC, "id"));
    final RelationLimitRuleVO relationLimitRuleVO = new RelationLimitRuleVO(relationLimitRules);
    return relationLimitRuleVO;
  }
}
