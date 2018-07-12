package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.TransactionalBiz;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.task.CreateTaskRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequireRole(AnnotationRoleStateEnum.admin)
public class CreateTaskBiz extends TransactionalBiz<CreateTaskRequest, AnnotationTask> {
  private final AnnotationTaskRepository taskRepository;

  public CreateTaskBiz(final AnnotationTaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @Override
  protected void validateRequest(CreateTaskRequest request) throws InvalidInputException {
    if (StringUtils.isBlank(request.getName())) {
      throw new InvalidInputException("invalid-taks-name", "task name should not be blank");
    }
  }

  @Override
  protected AnnotationTask doBiz(CreateTaskRequest request) {
    return taskRepository.save(new AnnotationTask(request.getName()));
  }
}
