package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AtomicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Created by cjl on 2018/6/1. */
public interface AtomicTermRepository
    extends JpaRepository<AtomicTerm, Integer>, JpaSpecificationExecutor {}
