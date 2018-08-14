package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.TaskBlock;
import cn.malgo.annotation.entity.TaskBlockId;
import cn.malgo.annotation.enums.AnnotationTaskState;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskBlockRepository
    extends JpaRepository<TaskBlock, TaskBlockId>, JpaSpecificationExecutor<TaskBlock> {
  Set<TaskBlock> findByTask_IdAndBlock_StateIn(long taskId, List<AnnotationTaskState> states);
}
