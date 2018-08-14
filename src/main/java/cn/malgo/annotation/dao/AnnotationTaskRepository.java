package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.entity.TaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnotationTaskRepository
    extends JpaRepository<AnnotationTask, Long>, JpaSpecificationExecutor<AnnotationTask> {

  default AnnotationTask updateState(final AnnotationTask task) {
    final Set<TaskBlock> taskBlocks = task.getTaskBlocks();

    if (taskBlocks.size() == 0) {
      return task;
    }

    final AnnotationTaskState state =
        task.getTaskBlocks()
            .stream()
            .min(Comparator.comparing(taskBlock -> taskBlock.getBlock().getState()))
            .get()
            .getBlock()
            .getState();

    if (state != task.getState()) {
      if (state == AnnotationTaskState.ANNOTATED || state == AnnotationTaskState.PRE_CLEAN) {
        task.setState(AnnotationTaskState.DOING);
      } else {
        task.setState(state);
      }
      return save(task);
    }

    return task;
  }

  List<AnnotationTask> findByStateNotIn(List<AnnotationTaskState> states);

  List<AnnotationTask> findByStateIn(List<AnnotationTaskState> states);

  List<AnnotationTask> findByStateIn(List<AnnotationTaskState> states, Sort sort);

  Set<AnnotationTask> findByTaskBlocks_Block_DocBlocks_DocEquals(OriginalDoc doc);
}
