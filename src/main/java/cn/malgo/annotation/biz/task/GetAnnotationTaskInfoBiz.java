package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.request.task.ListAnnotationTaskRequest;
import cn.malgo.annotation.vo.TaskInfoVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
@Slf4j
public class GetAnnotationTaskInfoBiz extends BaseBiz<ListAnnotationTaskRequest, List<TaskInfoVO>> {
  private final AnnotationTaskRepository annotationTaskRepository;

  public GetAnnotationTaskInfoBiz(final AnnotationTaskRepository annotationTaskRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  protected void validateRequest(ListAnnotationTaskRequest request) throws InvalidInputException {}

  @Override
  protected List<TaskInfoVO> doBiz(ListAnnotationTaskRequest request, UserDetails user) {
    return annotationTaskRepository
        .findAll(Sort.by(Sort.Direction.DESC, "createdTime"))
        .parallelStream()
        .map(
            annotationTask ->
                new TaskInfoVO(
                    annotationTask.getId(),
                    annotationTask.getName(),
                    annotationTask.getState().name()))
        .collect(Collectors.toList());
  }
}
