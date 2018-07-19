package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.ListAnnotationCombineRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.service.AnnotationCombineService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ListAnnotationBiz
    extends BaseBiz<ListAnnotationCombineRequest, PageVO<AnnotationCombineBratVO>> {

  private final AnnotationCombineService annotationCombineService;

  @Autowired
  public ListAnnotationBiz(AnnotationCombineService annotationCombineService) {
    this.annotationCombineService = annotationCombineService;
  }

  @Override
  protected void validateRequest(ListAnnotationCombineRequest listAnnotationCombineRequest)
      throws InvalidInputException {
    if (listAnnotationCombineRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (listAnnotationCombineRequest.getPageIndex() < 1) {
      throw new InvalidInputException("invalid-page-index", "pageIndex应该大于等于1");
    }
    if (listAnnotationCombineRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "pageSize应该大于等于0");
    }
  }

  @Override
  protected PageVO<AnnotationCombineBratVO> doBiz(
      ListAnnotationCombineRequest request, UserDetails user) {
    request.setPageIndex(request.getPageIndex() - 1);

    if (!user.hasPermission(Permissions.EXAMINE) && !user.hasPermission(Permissions.ADMIN)) {
      request.setUserId(user.getId());
    }

    final Page<AnnotationCombine> page = annotationCombineService.listAnnotationCombine(request);
    return new PageVO<>(
        page.getTotalElements(),
        AnnotationConvert.convert2AnnotationCombineBratVOList(page.getContent()));
  }
}
