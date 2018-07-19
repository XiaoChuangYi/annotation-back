package cn.malgo.annotation.biz.doc;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.doc.ListDocRequest;
import cn.malgo.annotation.result.PageVO;
import java.util.Collections;
import java.util.stream.Collectors;
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
  private final AnnotationTaskDocRepository annotationTaskDocRepository;

  public ListOriginalDocBiz(
      OriginalDocRepository originalDocRepository,
      AnnotationTaskDocRepository annotationTaskDocRepository) {
    this.originalDocRepository = originalDocRepository;
    this.annotationTaskDocRepository = annotationTaskDocRepository;
  }

  private static Specification<OriginalDoc> queryOriginalDocCondition(
      ListDocRequest param, List<Integer> docIds) {
    return (Specification<OriginalDoc>)
        (root, criteriaQuery, criteriaBuilder) -> {
          // todo 还会有其它的过滤条件
          List<Predicate> predicates = new ArrayList<>();
          if (StringUtils.isNotBlank(param.getName())) {
            predicates.add(
                criteriaBuilder.like(root.get("name"), String.format("%%%s%%", param.getName())));
          }
          if (StringUtils.isNotBlank(param.getType())) {
            predicates.add(
                criteriaBuilder.like(root.get("type"), String.format("%%%s%%", param.getType())));
          }
          if (StringUtils.isNotBlank(param.getText())) {
            predicates.add(
                criteriaBuilder.like(root.get("text"), String.format("%%%s%%", param.getText())));
          }
          if (StringUtils.isNotBlank(param.getSource())) {
            predicates.add(
                criteriaBuilder.like(
                    root.get("source"), String.format("%%%s%%", param.getSource())));
          }
          if (param.getMinTextLength() != 0) {
            predicates.add(criteriaBuilder.ge(root.get("textLength"), param.getMinTextLength()));
          }
          if (docIds.size() > 0) {
            predicates.add(criteriaBuilder.in(root.get("id")).value(docIds));
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
    List<Integer> docIdList = Collections.emptyList();
    if (listDocRequest.getTaskId() > 0) {
      docIdList =
          annotationTaskDocRepository
              .findAllByTask(new AnnotationTask(listDocRequest.getTaskId()))
              .stream()
              .map(annotationTaskDoc -> annotationTaskDoc.getDoc().getId())
              .collect(Collectors.toList());
    }
    if (listDocRequest.getDocId() > 0) {
      docIdList.clear();
      docIdList.add(listDocRequest.getDocId());
    }
    Page<OriginalDoc> page =
        originalDocRepository.findAll(
            queryOriginalDocCondition(listDocRequest, docIdList),
            PageRequest.of(pageIndex, listDocRequest.getPageSize()));
    PageVO<OriginalDoc> pageVO = new PageVO(page, false);
    pageVO.setDataList(page.getContent());
    return pageVO;
  }
}
