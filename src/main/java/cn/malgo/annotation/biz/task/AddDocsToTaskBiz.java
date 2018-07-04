package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.biz.AdminBaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.task.AddDocsToTaskRequest;
import cn.malgo.annotation.service.TaskDocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Slf4j
public class AddDocsToTaskBiz extends AdminBaseBiz<AddDocsToTaskRequest, AnnotationTask> {
  private final AnnotationTaskRepository taskRepository;
  private final OriginalDocRepository docRepository;
  private final TaskDocService taskDocService;

  public AddDocsToTaskBiz(
      final AnnotationTaskRepository taskRepository,
      final OriginalDocRepository docRepository,
      final TaskDocService taskDocService) {
    this.taskRepository = taskRepository;
    this.docRepository = docRepository;
    this.taskDocService = taskDocService;
  }

  @Override
  protected void validateRequest(AddDocsToTaskRequest request) throws InvalidInputException {
    if (request.getDocIds() == null || request.getDocIds().size() == 0) {
      throw new InvalidInputException("invalid-doc-ids", "doc ids should have value");
    }

    if (request.getAnnotationType() < 0
        || request.getAnnotationType() >= AnnotationTypeEnum.values().length) {
      throw new InvalidInputException(
          "invalid-annotation-type", "invalid annotation type: " + request.getAnnotationType());
    }
  }

  @Override
  @Transactional
  protected AnnotationTask doBiz(AddDocsToTaskRequest request) {
    final AnnotationTypeEnum annotationType =
        AnnotationTypeEnum.getByValue(request.getAnnotationType());

    try {
      final AnnotationTask task = taskRepository.getOne(request.getId());
      final List<OriginalDoc> docs = docRepository.findAllById(request.getDocIds());
      if (docs.size() != request.getDocIds().size()) {
        log.warn("some doc ids are invalid {}", request.getDocIds());
      }

      for (final OriginalDoc doc : docs) {
        taskDocService.addDocToTask(task, doc, annotationType);
      }

      return task;
    } catch (EntityNotFoundException ex) {
      throw new InvalidInputException("invalid-task-id", "invalid task id: " + request.getId());
    }
  }
}
