package cn.malgo.annotation.biz.doc;

import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.request.doc.ListDocRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.entity.BaseEntity;
import cn.malgo.service.exception.InvalidInputException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ListOriginalDocBiz extends BaseBiz<ListDocRequest, PageVO<OriginalDoc>> {
  private final OriginalDocRepository originalDocRepository;

  public ListOriginalDocBiz(final OriginalDocRepository originalDocRepository) {
    this.originalDocRepository = originalDocRepository;
  }

  private static Specification<OriginalDoc> queryOriginalDocCondition(
      ListDocRequest param, List<Long> docIds) {
    return (Specification<OriginalDoc>)
        (root, criteriaQuery, criteriaBuilder) -> {
          final List<Predicate> predicates = new ArrayList<>();

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

          if (param.getStates() != null && param.getStates().length != 0) {
            predicates.add(
                criteriaBuilder
                    .in(root.get("state"))
                    .value(
                        Arrays.stream(param.getStates())
                            .map(OriginalDocState::valueOf)
                            .collect(Collectors.toList())));
          }

          if (param.getMinTextLength() != 0) {
            predicates.add(criteriaBuilder.ge(root.get("textLength"), param.getMinTextLength()));
          }

          if (docIds.size() > 0) {
            predicates.add(criteriaBuilder.in(root.get("id")).value(docIds));
          }

          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
  }

  @Override
  protected void validateRequest(ListDocRequest request) throws InvalidInputException {
    if (request.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }

    if (request.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
  }

  @Override
  protected PageVO<OriginalDoc> doBiz(ListDocRequest request) {
    final int pageIndex = request.getPageIndex() - 1;
    final List<Long> docIdList = new ArrayList<>();

    if (request.getTaskId() > 0 && request.getDocId() <= 0) {
      docIdList.addAll(
          originalDocRepository
              .findByBlocks_Block_TaskBlocks_Task_IdEquals(request.getTaskId())
              .stream()
              .map(BaseEntity::getId)
              .collect(Collectors.toList()));

      if (docIdList.size() == 0) {
        return new PageVO<>(0, Collections.emptyList());
      }
    } else if (request.getDocId() > 0) {
      docIdList.add(request.getDocId());
    }

    return new PageVO<>(
        originalDocRepository.findAll(
            queryOriginalDocCondition(request, docIdList),
            PageRequest.of(pageIndex, request.getPageSize())));
  }
}
