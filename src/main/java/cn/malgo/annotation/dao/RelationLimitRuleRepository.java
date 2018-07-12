package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.RelationLimitRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RelationLimitRuleRepository
    extends JpaRepository<RelationLimitRule, Integer>, JpaSpecificationExecutor {

  RelationLimitRule findBySourceEqualsAndTargetEqualsAndRelationTypeEquals(
      String source, String target, String relationType);

  default boolean isLegalRelation(String source, String target, String relationType) {
    return findBySourceEqualsAndTargetEqualsAndRelationTypeEquals(source, target, relationType)
            == null
        ? true
        : false;
  }
}
