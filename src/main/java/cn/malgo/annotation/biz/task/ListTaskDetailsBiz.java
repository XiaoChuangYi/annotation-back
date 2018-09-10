package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.request.task.ListTaskDetailRequest;
import cn.malgo.annotation.vo.AnnotationTaskBlockResponse;
import cn.malgo.annotation.vo.AnnotationTaskDetailVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ListTaskDetailsBiz extends BaseBiz<ListTaskDetailRequest, AnnotationTaskDetailVO> {

  private final AnnotationTaskRepository annotationTaskRepository;

  public ListTaskDetailsBiz(final AnnotationTaskRepository annotationTaskRepository) {
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
    final List<AnnotationTaskBlockResponse> blocks =
        annotationTask
            .getTaskBlocks()
            .stream()
            .map(taskBlock -> new AnnotationTaskBlockResponse(taskBlock.getBlock(), "", false))
            .collect(Collectors.toList());
    return new AnnotationTaskDetailVO(
        annotationTask.getName(), annotationTask.getState().name(), blocks);
  }
}
