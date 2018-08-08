package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.request.ListAnnotationRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.service.AnnotationService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBratVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ListAnnotationBiz extends BaseBiz<ListAnnotationRequest, PageVO<AnnotationBratVO>> {

  private final AnnotationService annotationService;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  @Autowired
  public ListAnnotationBiz(
      final AnnotationService annotationService,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationService = annotationService;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(ListAnnotationRequest request) throws InvalidInputException {
    if (request == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (request.getPageIndex() < 1) {
      throw new InvalidInputException("invalid-page-index", "pageIndex应该大于等于1");
    }
    if (request.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "pageSize应该大于等于0");
    }
  }

  @Override
  protected PageVO<AnnotationBratVO> doBiz(ListAnnotationRequest request, UserDetails user) {
    request.setPageIndex(request.getPageIndex() - 1);

    if (!user.hasPermission(Permissions.EXAMINE) && !user.hasPermission(Permissions.ADMIN)) {
      request.setUserId(user.getId());
    }
    final Page<AnnotationNew> page = annotationService.listAnnotationNew(request);
    final List<AnnotationBratVO> annotationBratVOS =
        page.getContent()
            .parallelStream()
            .map(
                annotationNew -> {
                  AnnotationBratVO annotationBratVO =
                      AnnotationConvert.convert2AnnotationBratVO(annotationNew);
                  AnnotationTaskBlock annotationTaskBlock =
                      annotationTaskBlockRepository.findById(annotationNew.getBlockId()).get();
                  if (annotationTaskBlock != null) {
                    annotationBratVO.setReviewedAnnotation(
                        AnnotationConvert.convertAnnotation2BratFormat(
                            annotationTaskBlock.getText(),
                            annotationTaskBlock.getAnnotation(),
                            annotationTaskBlock.getAnnotationType().ordinal()));
                  }
                  return annotationBratVO;
                })
            .collect(Collectors.toList());
    return new PageVO<>(page.getTotalElements(), annotationBratVOS);
  }
}
