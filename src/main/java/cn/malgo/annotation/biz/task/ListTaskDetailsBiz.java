package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.task.ListTaskDetailRequest;
import cn.malgo.annotation.vo.AnnotationTaskDetailVO;
import cn.malgo.annotation.vo.OriginalDocVO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class ListTaskDetailsBiz extends BaseBiz<ListTaskDetailRequest, AnnotationTaskDetailVO> {

  private final AnnotationTaskDocRepository annotationTaskDocRepository;
  private final OriginalDocRepository originalDocRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public ListTaskDetailsBiz(
      AnnotationTaskDocRepository annotationTaskDocRepository,
      OriginalDocRepository originalDocRepository,
      AnnotationTaskRepository annotationTaskRepository) {
    this.annotationTaskDocRepository = annotationTaskDocRepository;
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
  protected AnnotationTaskDetailVO doBiz(
      int userId, int role, ListTaskDetailRequest listTaskDetailRequest) {
    AnnotationTaskDetailVO annotationTaskDetailVO;
    List<OriginalDocVO> originalDocVOList = new ArrayList<>();
    // task任务还没有文档详情的情况
    final AnnotationTask annotationTask =
        annotationTaskRepository.getOne(listTaskDetailRequest.getId());
    final List<AnnotationTaskDoc> annotationTaskDocList =
        annotationTaskDocRepository.findAllByTask(
            new AnnotationTask(listTaskDetailRequest.getId()));
    if (annotationTaskDocList.size() > 0) {
      final List<Integer> idList =
          annotationTaskDocList.stream().map(x -> x.getDoc().getId()).collect(Collectors.toList());
      originalDocVOList =
          originalDocRepository
              .findAllById(idList)
              .stream()
              .map(x -> new OriginalDocVO(x.getId(), x.getType(), x.getState().name()))
              .collect(Collectors.toList());
    }
    annotationTaskDetailVO =
        new AnnotationTaskDetailVO(
            annotationTask.getName(),
            annotationTask.getState().name(),
            originalDocVOList,
            originalDocVOList.size(),
            annotationTask
                .getTaskDocs()
                .stream()
                .flatMap(annotationTaskDoc -> annotationTaskDoc.getBlocks().stream())
                .count());
    return annotationTaskDetailVO;
  }
}
