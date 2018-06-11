package com.malgo.biz;

import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.ListAnnotationCombineRequest;
import com.malgo.result.PageVO;
import com.malgo.service.AnnotationCombineService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.AnnotationCombineBratVO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/30. */
@Component
public class ListAnnotationBiz
    extends BaseBiz<ListAnnotationCombineRequest, PageVO<AnnotationCombineBratVO>> {

  private final AnnotationCombineService annotationCombineService;
  private int globalUserId;
  private int globalRoleId;

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
  protected void authorize(
      int userId, int role, ListAnnotationCombineRequest listAnnotationCombineRequest)
      throws BusinessRuleException {
    globalUserId = userId;
    globalRoleId = role;
  }

  @Override
  protected PageVO<AnnotationCombineBratVO> doBiz(
      ListAnnotationCombineRequest annotationCombineQuery) {
    annotationCombineQuery.setPageIndex(annotationCombineQuery.getPageIndex() - 1);
    if (globalRoleId > 2) {
      annotationCombineQuery.setUserId(globalUserId);
    }
    Page page = annotationCombineService.listAnnotationCombine(annotationCombineQuery);
    List<AnnotationCombineBratVO> annotationCombineBratVOList =
        AnnotationConvert.convert2AnnotationCombineBratVOList(page.getContent());
    PageVO pageVO = new PageVO(page, false);
    pageVO.setDataList(annotationCombineBratVOList);
    return pageVO;
  }
}
