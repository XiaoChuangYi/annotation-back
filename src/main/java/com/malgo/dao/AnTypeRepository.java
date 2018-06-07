package com.malgo.dao;

import com.malgo.entity.AnType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by cjl on 2018/6/5.
 */
public interface AnTypeRepository extends JpaRepository<AnType,Integer>,JpaSpecificationExecutor {

}
