package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.request.task.CreateTaskRequest;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CreateTaskBiz extends TransactionalBiz<CreateTaskRequest, AnnotationTaskVO> {
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
  protected AnnotationTaskVO doBiz(CreateTaskRequest request) {
    return new AnnotationTaskVO(taskRepository.save(new AnnotationTask(request.getName())));
  }
}
