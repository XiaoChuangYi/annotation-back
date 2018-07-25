package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.request.task.TerminateTaskRequest;
import cn.malgo.annotation.vo.TaskStateVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class QueryTaskStateBiz extends BaseBiz<TerminateTaskRequest, TaskStateVO> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final OriginalDocRepository originalDocRepository;

  public QueryTaskStateBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final OriginalDocRepository originalDocRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.originalDocRepository = originalDocRepository;
  }

  @Override
  protected void validateRequest(TerminateTaskRequest terminateTaskRequest)
      throws InvalidInputException {
    if (terminateTaskRequest.getTaskId() <= 0) {
      throw new InvalidInputException("invalid-task-id", "无效的taskId");
    }
  }

  @Override
  protected TaskStateVO doBiz(TerminateTaskRequest terminateTaskRequest, UserDetails user) {
    final Set<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findByStateInAndTaskBlocks_Task_Id(
            Collections.singletonList(AnnotationTaskState.DOING), terminateTaskRequest.getTaskId());
    final Map<OriginalDocState, List<OriginalDoc>> map =
        originalDocRepository
            .findByBlocks_Block_TaskBlocks_Task_IdEquals(terminateTaskRequest.getTaskId())
            .stream()
            .collect(Collectors.groupingBy(OriginalDoc::getState));
    return new TaskStateVO(
        annotationTaskBlocks.size(),
        map.getOrDefault(OriginalDocState.IMPORTED, Collections.emptyList()).size(),
        map.getOrDefault(OriginalDocState.PROCESSING, Collections.emptyList()).size(),
        map.getOrDefault(OriginalDocState.PROCESSED, Collections.emptyList()).size());
  }
}
