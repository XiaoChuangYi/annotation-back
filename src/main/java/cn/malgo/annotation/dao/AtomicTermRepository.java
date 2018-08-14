package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AtomicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AtomicTermRepository
    extends JpaRepository<AtomicTerm, Long>, JpaSpecificationExecutor {}
