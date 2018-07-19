package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnTypeRepository
    extends JpaRepository<AnType, Long>, JpaSpecificationExecutor {}
