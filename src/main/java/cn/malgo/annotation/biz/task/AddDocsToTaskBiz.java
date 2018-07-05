package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.TransactionalBiz;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.task.AddDocsToTaskRequest;
import cn.malgo.annotation.service.TaskDocService;
import cn.malgo.annotation.service.impl.TaskDocServiceImpl;
import cn.malgo.annotation.vo.AddDocsToTaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Slf4j
@RequireRole(AnnotationRoleStateEnum.admin)
public class AddDocsToTaskBiz
    extends TransactionalBiz<AddDocsToTaskRequest, AddDocsToTaskResponse> {
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
  protected AddDocsToTaskResponse doBiz(AddDocsToTaskRequest request) {
    final AnnotationTypeEnum annotationType =
        AnnotationTypeEnum.getByValue(request.getAnnotationType());

    try {
      final AnnotationTask task = taskRepository.getOne(request.getId());
      final List<OriginalDoc> docs = docRepository.findAllById(request.getDocIds());
      if (docs.size() != request.getDocIds().size()) {
        log.warn("some doc ids are invalid {}", request.getDocIds());
      }

      int createdBlocks = 0;
      for (final OriginalDoc doc : docs) {
        final TaskDocServiceImpl.AddDocResult addDocResult =
            taskDocService.addDocToTask(task, doc, annotationType);
        createdBlocks += addDocResult.getCreatedBlocks();
        doc.setState(OriginalDocState.PROCESSING);
      }

      if (docs.size() != 0) {
        if (createdBlocks != 0) {
          task.setState(AnnotationTaskState.DOING);
        } else if (task.getState() != AnnotationTaskState.DOING) {
          // 如果没有生成任何block，而且之前不是DOING状态，则设置状态为标注完成，跳过DOING状态
          task.setState(AnnotationTaskState.ANNOTATED);
        }

        docRepository.saveAll(docs);
      }

      return new AddDocsToTaskResponse(task, createdBlocks);
    } catch (EntityNotFoundException ex) {
      throw new InvalidInputException("invalid-task-id", "invalid task id: " + request.getId());
    }
  }
}
