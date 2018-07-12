package cn.malgo.annotation.biz;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.ListAnnotationCombineRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.service.AnnotationCombineService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/30. */
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
      int userId, int role, ListAnnotationCombineRequest annotationCombineQuery) {
    annotationCombineQuery.setPageIndex(annotationCombineQuery.getPageIndex() - 1);
    if (role > AnnotationRoleStateEnum.auditor.getRole()) {
      annotationCombineQuery.setUserId(userId);
    }
    Page page = annotationCombineService.listAnnotationCombine(annotationCombineQuery);
    List<AnnotationCombineBratVO> annotationCombineBratVOList =
        AnnotationConvert.convert2AnnotationCombineBratVOList(page.getContent());
    PageVO pageVO = new PageVO(page, false);
    pageVO.setDataList(annotationCombineBratVOList);
    return pageVO;
  }
}
