package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.OriginalDocState;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OriginalDocRepository
    extends JpaRepository<OriginalDoc, Long>, JpaSpecificationExecutor<OriginalDoc> {

  Set<OriginalDoc> findByBlocks_Block_TaskBlocks_Task_IdEquals(long taskId);

  List<OriginalDoc> findAllBySourceEquals(String source);

  List<OriginalDoc> findAllBySourceEqualsAndStateEquals(String source, OriginalDocState state);
}
