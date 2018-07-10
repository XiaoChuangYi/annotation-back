package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.RelationLimitRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RelationLimitRuleRepository
    extends JpaRepository<RelationLimitRule, Integer>, JpaSpecificationExecutor {}
