package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Comparator;
import java.util.List;

public interface AnnotationTaskDocRepository extends JpaRepository<AnnotationTaskDoc, Long> {
  default AnnotationTaskDoc updateState(final AnnotationTaskDoc taskDoc) {
    if (taskDoc.getBlocks().size() == 0) {
      throw new IllegalStateException("task doc " + taskDoc.getId() + " has no block");
    }

    // TaskDoc的状态是所有Block状态中的最小值
    final AnnotationTaskState state =
        taskDoc
            .getBlocks()
            .stream()
            .min(Comparator.comparing(lhs -> lhs.getBlock().getState()))
            .get()
            .getBlock()
            .getState();

    taskDoc.setState(state);
    return save(taskDoc);
  }

  List<AnnotationTaskDoc> findByTask_Id(long taskId);

  List<AnnotationTaskDoc> findByDoc_Id(long docId);
}
