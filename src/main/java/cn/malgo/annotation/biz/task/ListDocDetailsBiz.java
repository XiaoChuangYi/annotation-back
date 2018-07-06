package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.doc.ListDocDetailRequest;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.annotation.vo.OriginalDocDetailVO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class ListDocDetailsBiz extends BaseBiz<ListDocDetailRequest, OriginalDocDetailVO> {

  private final AnnotationTaskDocRepository annotationTaskDocRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public ListDocDetailsBiz(
      AnnotationTaskDocRepository annotationTaskDocRepository,
      AnnotationTaskRepository annotationTaskRepository) {
    this.annotationTaskDocRepository = annotationTaskDocRepository;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  protected void validateRequest(ListDocDetailRequest listDocDetailRequest)
      throws InvalidInputException {
    if (listDocDetailRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (listDocDetailRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的参数id");
    }
  }

  @Override
  protected OriginalDocDetailVO doBiz(
      int userId, int role, ListDocDetailRequest listDocDetailRequest) {
    final OriginalDocDetailVO originalDocDetailVO;
    // 查询task-doc关系表
    final List<AnnotationTaskDoc> annotationTaskDocList =
        annotationTaskDocRepository.findAllByDoc(new OriginalDoc(listDocDetailRequest.getId()));
    // 获取taskId集合
    final List<Integer> taskIdList =
        annotationTaskDocList.stream().map(x -> x.getTask().getId()).collect(Collectors.toList());
    // 查询task集合
    final List<AnnotationTask> annotationTaskList =
        annotationTaskRepository.findAllById(taskIdList);
    // 封装到vo对象
    final List<AnnotationTaskVO> annotationTaskVOList =
        annotationTaskList
            .stream()
            .map(
                x ->
                    new AnnotationTaskVO(
                        x.getId(),
                        x.getCreatedTime(),
                        x.getLastModified(),
                        x.getName(),
                        x.getState().name()))
            .collect(Collectors.toList());
    // 封装到最终对象
    originalDocDetailVO =
        new OriginalDocDetailVO(
            listDocDetailRequest.getId(),
            annotationTaskDocList.get(0).getDoc().getCreatedTime(),
            annotationTaskVOList);
    return originalDocDetailVO;
  }
}
