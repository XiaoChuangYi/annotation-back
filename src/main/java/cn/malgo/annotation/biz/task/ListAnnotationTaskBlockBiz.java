package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.task.ListAnnotationTaskBlockRequest;
import cn.malgo.annotation.result.PageVO;
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
public class ListAnnotationTaskBlockBiz
    extends BaseBiz<ListAnnotationTaskBlockRequest, PageVO<AnnotationTaskBlock>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public ListAnnotationTaskBlockBiz(AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  private static Specification<AnnotationTaskBlock> queryAnnotationTaskBlockCondition(
      ListAnnotationTaskBlockRequest param) {
    return (Specification<AnnotationTaskBlock>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (StringUtils.isNotBlank(param.getText())) {
            predicates.add(
                criteriaBuilder.like(
                    root.get("text"), String.format("%s%s%s", "%", param.getText(), "%")));
          }
          if (param.getStates() != null
              && param.getStates().size() > 0
              && !param.getStates().contains(null)) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(param.getStates()));
          }
          if (param.getAnnotationTypes() != null
              && param.getAnnotationTypes().size() > 0
              && !param.getAnnotationTypes().contains("")) {
            final List<AnnotationTypeEnum> annotationTypes =
                param
                    .getAnnotationTypes()
                    .stream()
                    .map(x -> AnnotationTypeEnum.valueOf(x))
                    .collect(Collectors.toList());
            predicates.add(criteriaBuilder.in(root.get("annotationType")).value(annotationTypes));
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
  }

  @Override
  protected void validateRequest(ListAnnotationTaskBlockRequest listAnnotationTaskBlockRequest)
      throws InvalidInputException {
    if (listAnnotationTaskBlockRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (listAnnotationTaskBlockRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }
    if (listAnnotationTaskBlockRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
  }

  @Override
  protected PageVO<AnnotationTaskBlock> doBiz(
      int userId, int role, ListAnnotationTaskBlockRequest listAnnotationTaskBlockRequest) {
    final int pageIndex = listAnnotationTaskBlockRequest.getPageIndex() - 1;
    Page<AnnotationTaskBlock> page =
        annotationTaskBlockRepository.findAll(
            queryAnnotationTaskBlockCondition(listAnnotationTaskBlockRequest),
            PageRequest.of(pageIndex, listAnnotationTaskBlockRequest.getPageSize()));
    PageVO<AnnotationTaskBlock> pageVO = new PageVO(page, false);
    pageVO.setDataList(page.getContent());
    return pageVO;
  }
}
