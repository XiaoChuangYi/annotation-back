package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.RelationLimitRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RelationLimitRuleRepository
    extends JpaRepository<RelationLimitRule, Long>, JpaSpecificationExecutor {

  RelationLimitRule findBySourceAndTargetAndRelationType(
      String source, String target, String relationType);

  default boolean isLegalRelation(String source, String target, String relationType) {
    return findBySourceAndTargetAndRelationType(source, target, relationType) == null;
  }

  List<RelationLimitRule> findBySourceAndTarget(String source, String target);
}
