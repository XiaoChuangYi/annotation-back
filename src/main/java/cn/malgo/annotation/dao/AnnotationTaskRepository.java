package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Comparator;
import java.util.List;

public interface AnnotationTaskRepository
    extends JpaRepository<AnnotationTask, Long>, JpaSpecificationExecutor<AnnotationTask> {
  default AnnotationTask updateState(final AnnotationTask task) {
    if (task.getTaskDocs().size() == 0) {
      return task;
    }

    final AnnotationTaskState state =
        task.getTaskDocs()
            .stream()
            .min(Comparator.comparing(AnnotationTaskDoc::getState))
            .get()
            .getState();

    if (state != task.getState()) {
      task.setState(state);
      return save(task);
    }

    return task;
  }

  List<AnnotationTask> findByStateNotIn(List<AnnotationTaskState> states);

  @EntityGraph(value = "graph.annotation", type = EntityGraphType.FETCH)
  AnnotationTask findAllById(Long taskId);
}
