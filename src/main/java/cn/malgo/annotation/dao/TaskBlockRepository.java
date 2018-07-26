package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.TaskBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskBlockRepository
    extends JpaRepository<TaskBlock, Long>, JpaSpecificationExecutor<TaskBlock> {}
