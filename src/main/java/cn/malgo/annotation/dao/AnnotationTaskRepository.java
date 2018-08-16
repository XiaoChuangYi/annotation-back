package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.entity.TaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnotationTaskRepository
    extends JpaRepository<AnnotationTask, Long>, JpaSpecificationExecutor<AnnotationTask> {

  default AnnotationTask updateState(final AnnotationTask task) {
    final Set<TaskBlock> taskBlocks = task.getTaskBlocks();

    if (taskBlocks.size() == 0) {
      return task;
    } else {
      if (task.getState() == AnnotationTaskState.CREATED) {
        task.setState(AnnotationTaskState.DOING);
      } else if (StringUtils.equalsAny(
          task.getState().name(),
          AnnotationTaskState.DOING.name(),
          AnnotationTaskState.ANNOTATED.name(),
          AnnotationTaskState.PRE_CLEAN.name())) {
        task.setState(AnnotationTaskState.DOING);
      } else {
        task.setState(AnnotationTaskState.FINISHED);
      }
      return save(task);
    }
  }

  List<AnnotationTask> findByStateNotIn(List<AnnotationTaskState> states);

  List<AnnotationTask> findByStateIn(List<AnnotationTaskState> states);

  List<AnnotationTask> findByStateIn(List<AnnotationTaskState> states, Sort sort);

  Set<AnnotationTask> findByTaskBlocks_Block_DocBlocks_DocEquals(OriginalDoc doc);
}
