package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Created by cjl on 2018/6/5. */
public interface AnTypeRepository
    extends JpaRepository<AnType, Integer>, JpaSpecificationExecutor {}
