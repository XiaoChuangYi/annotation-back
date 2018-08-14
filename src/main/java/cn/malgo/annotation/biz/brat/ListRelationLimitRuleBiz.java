package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.vo.RelationLimitRuleVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
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
    return new RelationLimitRuleVO(
        relationLimitRuleRepository.findAll(Sort.by(Direction.ASC, "id")));
  }
}
