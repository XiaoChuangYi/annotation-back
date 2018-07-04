package cn.malgo.annotation.biz.doc;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.doc.ListDocRequest;
import cn.malgo.annotation.result.PageVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class ListOriginalDocBiz extends BaseBiz<ListDocRequest, PageVO<OriginalDoc>> {

  private final OriginalDocRepository originalDocRepository;

  public ListOriginalDocBiz(OriginalDocRepository originalDocRepository) {
    this.originalDocRepository = originalDocRepository;
  }

  private static Specification<OriginalDoc> queryOriginalDocCondition(ListDocRequest param) {
    return (Specification<OriginalDoc>)
        (root, criteriaQuery, criteriaBuilder) -> {
          // todo 还会有其它的过滤条件
          List<Predicate> predicates = new ArrayList<>();
          if (StringUtils.isNotBlank(param.getText())) {
            predicates.add(
                criteriaBuilder.like(root.get("text"), String.format("%{}%", param.getText())));
          }
          if (StringUtils.isNotBlank(param.getType())) {
            predicates.add(
                criteriaBuilder.like(root.get("type"), String.format("%{}%", param.getType())));
          }
          if (StringUtils.isNotBlank(param.getName())) {
            predicates.add(
                criteriaBuilder.like(root.get("text"), String.format("%{}%", param.getText())));
          }
          if (StringUtils.isNotBlank(param.getSource())) {
            predicates.add(
                criteriaBuilder.like(root.get("source"), String.format("%{}%", param.getSource())));
          }
          if (param.getDocState().size() > 0) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(param.getDocState()));
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
  }

  @Override
  protected void validateRequest(ListDocRequest listDocRequest) throws InvalidInputException {
    if (listDocRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (listDocRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }
    if (listDocRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
  }

  @Override
  protected PageVO<OriginalDoc> doBiz(int userId, int role, ListDocRequest listDocRequest) {
    final int pageIndex = listDocRequest.getPageIndex() - 1;
    Page page =
        originalDocRepository.findAll(
            queryOriginalDocCondition(listDocRequest),
            PageRequest.of(pageIndex, listDocRequest.getPageSize()));
    PageVO pageVO = new PageVO(page, false);
    pageVO.setDataList(page.getContent());
    return pageVO;
  }
}
