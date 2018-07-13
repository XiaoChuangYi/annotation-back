package cn.malgo.annotation.dao;

import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Comparator;

public interface AnnotationTaskRepository
    extends JpaRepository<AnnotationTask, Integer>, JpaSpecificationExecutor {

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

  default PageVO<AnnotationTaskVO> listAnnotationTasks(
      Specification<AnnotationTask> specification, Pageable pageable) {
    final Page<AnnotationTask> page = findAll(specification, pageable);
    final PageVO<AnnotationTaskVO> pageVO = new PageVO(page, false);
    final List<AnnotationTaskVO> annotationTaskVOList =
        page.getContent()
            .stream()
            .map(
                annotationTask ->
                    new AnnotationTaskVO(
                        annotationTask.getId(),
                        annotationTask.getCreatedTime(),
                        annotationTask.getLastModified(),
                        annotationTask.getName(),
                        annotationTask.getState().name(),
                        0,
                        annotationTask
                            .getTaskDocs()
                            .stream()
                            .flatMap(annotationTaskDoc -> annotationTaskDoc.getBlocks().stream())
                            .count()))
            .collect(Collectors.toList());
    pageVO.setDataList(annotationTaskVOList);
    return pageVO;
  }
}
