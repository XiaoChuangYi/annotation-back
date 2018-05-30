package com.malgo.base;

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

/**
 * Created by cjl on 2018/5/30.
 */
@Component
public class ListAnnotationBiz extends
    BaseBiz<ListAnnotationCombineRequest, PageVO<AnnotationCombineBratVO>> {

  private final AnnotationCombineService annotationCombineService;


  @Autowired
  public ListAnnotationBiz(AnnotationCombineService annotationCombineService) {
    this.annotationCombineService = annotationCombineService;
  }

  @Override
  protected void validateRequest(ListAnnotationCombineRequest listAnnotationCombineRequest)
      throws InvalidInputException {
    if(listAnnotationCombineRequest.getPageIndex()<1)
      throw new InvalidInputException("invalid-pageIndex", "pageIndex应该大于等于1");
    if(listAnnotationCombineRequest.getPageSize()<=0)
      throw new InvalidInputException("invalid-pageSize", "pageSize应该大于等于1");

  }

  @Override
  protected void authorize(String authToken, ListAnnotationCombineRequest listAnnotationCombineRequest)
      throws BusinessRuleException {

  }

  @Override
  protected PageVO<AnnotationCombineBratVO> doBiz(ListAnnotationCombineRequest annotationCombineQuery) {
    annotationCombineQuery.setPageIndex(annotationCombineQuery.getPageIndex()-1);
    Page page = annotationCombineService.listAnnotationCombine(annotationCombineQuery);
    List<AnnotationCombineBratVO> annotationCombineBratVOList = AnnotationConvert
        .convert2AnnotationCombineBratVOList(page.getContent());
    PageVO pageVO=new PageVO(page,false);
    pageVO.setDataList(annotationCombineBratVOList);
    return pageVO;
  }
}
