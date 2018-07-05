package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Comparator;

public interface AnnotationTaskDocRepository extends JpaRepository<AnnotationTaskDoc, Integer> {
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
}
