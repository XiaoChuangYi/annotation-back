package com.malgo.dao;

import com.malgo.entity.AtomicTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by cjl on 2018/6/1.
 */
public interface AtomicTermRepository extends JpaRepository<AtomicTerm,Integer>,JpaSpecificationExecutor {

}
