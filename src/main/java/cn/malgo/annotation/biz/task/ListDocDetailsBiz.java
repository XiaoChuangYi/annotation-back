package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTaskDoc;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.request.doc.ListDocDetailRequest;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.annotation.vo.OriginalDocDetailVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequirePermission(Permissions.ADMIN)
public class ListDocDetailsBiz extends BaseBiz<ListDocDetailRequest, OriginalDocDetailVO> {
  private final AnnotationTaskDocRepository annotationTaskDocRepository;
  private final AnnotationTaskRepository annotationTaskRepository;
  private final OriginalDocRepository originalDocRepository;

  public ListDocDetailsBiz(
      AnnotationTaskDocRepository annotationTaskDocRepository,
      AnnotationTaskRepository annotationTaskRepository,
      OriginalDocRepository originalDocRepository) {
    this.annotationTaskDocRepository = annotationTaskDocRepository;
    this.annotationTaskRepository = annotationTaskRepository;
    this.originalDocRepository = originalDocRepository;
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
  protected OriginalDocDetailVO doBiz(ListDocDetailRequest request) {
    List<AnnotationTaskVO> annotationTaskVOList = new ArrayList<>();
    final OriginalDoc originalDoc = originalDocRepository.getOne(request.getId());
    // 查询task-doc关系表
    final List<AnnotationTaskDoc> annotationTaskDocList =
        annotationTaskDocRepository.findByDoc_Id(request.getId());
    // 获取taskId集合
    if (annotationTaskDocList.size() > 0) {
      final List<Long> taskIdList =
          annotationTaskDocList.stream().map(x -> x.getTask().getId()).collect(Collectors.toList());
      // 查询task集合,封装到vo对象
      annotationTaskVOList =
          annotationTaskRepository
              .findAllById(taskIdList)
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
    }
    // 封装到最终对象
    return new OriginalDocDetailVO(
        originalDoc.getId(), originalDoc.getCreatedTime(), annotationTaskVOList);
  }
}
