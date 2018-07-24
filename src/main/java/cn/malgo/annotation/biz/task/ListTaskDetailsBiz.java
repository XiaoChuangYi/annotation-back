package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.request.task.ListTaskDetailRequest;
import cn.malgo.annotation.vo.AnnotationTaskDetailVO;
import cn.malgo.annotation.vo.OriginalDocVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequirePermission(Permissions.ADMIN)
public class ListTaskDetailsBiz extends BaseBiz<ListTaskDetailRequest, AnnotationTaskDetailVO> {

  private final OriginalDocRepository originalDocRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public ListTaskDetailsBiz(
      OriginalDocRepository originalDocRepository,
      AnnotationTaskRepository annotationTaskRepository) {
    this.originalDocRepository = originalDocRepository;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  protected void validateRequest(ListTaskDetailRequest listTaskDetailRequest)
      throws InvalidInputException {
    if (listTaskDetailRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationTaskDetailVO doBiz(ListTaskDetailRequest request, UserDetails user) {
    final AnnotationTask annotationTask = annotationTaskRepository.getOne(request.getId());
    final List<OriginalDocVO> originalDocVOList =
        originalDocRepository
            .findByTasks_Task_IdEquals(request.getId())
            .stream()
            .map(
                originalDoc ->
                    new OriginalDocVO(
                        originalDoc.getId(), originalDoc.getType(), originalDoc.getState().name()))
            .collect(Collectors.toList());

    return new AnnotationTaskDetailVO(
        annotationTask.getName(),
        annotationTask.getState().name(),
        originalDocVOList,
        originalDocVOList.size(),
        annotationTask.getTotalBranchNum());
  }
}
