package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.ListOverlapEntityRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@RequirePermission(Permissions.ADMIN)
@Component
public class ListOverlapEntityBiz
    extends BaseBiz<ListOverlapEntityRequest, PageVO<AnnotationBlockBratVO>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public ListOverlapEntityBiz(final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(ListOverlapEntityRequest listOverlapEntityRequest)
      throws InvalidInputException {
    if (listOverlapEntityRequest.getTaskId() <= 0) {
      throw new InvalidInputException("invalid-task-id", "无效的taskId");
    }
    if (listOverlapEntityRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的pageIndex");
    }
    if (listOverlapEntityRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的pageSize");
    }
  }

  @Override
  protected PageVO<AnnotationBlockBratVO> doBiz(
      ListOverlapEntityRequest listOverlapEntityRequest, UserDetails user) {
    final int skip =
        (listOverlapEntityRequest.getPageIndex() - 1) * listOverlapEntityRequest.getPageSize();
    final int limit = listOverlapEntityRequest.getPageSize();
    final Set<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findByAnnotationTypeAndStateInAndTaskDocs_TaskDoc_Task_Id(
            AnnotationTypeEnum.relation,
            Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.FINISHED),
            listOverlapEntityRequest.getTaskId());
    final List<AnnotationTaskBlock> annotationTaskBlockList =
        annotationTaskBlocks
            .stream()
            .filter(
                annotationTaskBlock ->
                    AnnotationConvert.isCrossAnnotation(annotationTaskBlock.getAnnotation()))
            .collect(Collectors.toList());
    PageVO<AnnotationBlockBratVO> pageVO = new PageVO<>();
    pageVO.setTotal(annotationTaskBlockList.size());
    pageVO.setDataList(
        annotationTaskBlockList
            .stream()
            .skip(skip)
            .limit(limit)
            .map(
                annotationTaskBlock ->
                    AnnotationConvert.convert2AnnotationBlockBratVO(annotationTaskBlock))
            .collect(Collectors.toList()));
    return pageVO;
  }
}
