package cn.malgo.annotation.biz.doc;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.request.doc.ListDocDetailRequest;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.annotation.vo.OriginalDocDetailVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class ListDocDetailsBiz extends BaseBiz<ListDocDetailRequest, OriginalDocDetailVO> {
  private final AnnotationTaskRepository annotationTaskRepository;
  private final OriginalDocRepository originalDocRepository;

  public ListDocDetailsBiz(
      AnnotationTaskRepository annotationTaskRepository,
      OriginalDocRepository originalDocRepository) {
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
    final OriginalDoc originalDoc = originalDocRepository.getOne(request.getId());
    final List<AnnotationTaskVO> tasks =
        annotationTaskRepository
            .findByTaskBlocks_Block_DocBlocks_DocEquals(originalDoc)
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
    return new OriginalDocDetailVO(originalDoc.getId(), originalDoc.getCreatedTime(), tasks);
  }
}
