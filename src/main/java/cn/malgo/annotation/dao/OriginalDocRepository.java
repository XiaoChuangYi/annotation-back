package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.OriginalDoc;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OriginalDocRepository
    extends JpaRepository<OriginalDoc, Long>, JpaSpecificationExecutor<OriginalDoc> {
  Set<OriginalDoc> findByBlocks_Block_TaskBlocks_Task_IdEquals(long taskId);
}
