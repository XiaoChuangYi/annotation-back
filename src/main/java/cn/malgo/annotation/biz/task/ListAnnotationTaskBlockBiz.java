package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.task.ListAnnotationTaskBlockRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationTaskBlockResponse;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequirePermission(Permissions.ADMIN)
public class ListAnnotationTaskBlockBiz
    extends BaseBiz<ListAnnotationTaskBlockRequest, PageVO<AnnotationTaskBlockResponse>> {
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public ListAnnotationTaskBlockBiz(AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  private Specification<AnnotationTaskBlock> queryAnnotationTaskBlockCondition(
      ListAnnotationTaskBlockRequest request) {
    return (Specification<AnnotationTaskBlock>)
        (root, criteriaQuery, criteriaBuilder) -> {
          final List<Predicate> predicates = new ArrayList<>();

          if (request.getId() != null && request.getId() != 0) {
            predicates.add(criteriaBuilder.equal(root.get("id"), request.getId()));
          }

          if (request.getStates() != null && request.getStates().size() > 0) {
            predicates.add(
                criteriaBuilder
                    .in(root.get("state"))
                    .value(
                        request
                            .getStates()
                            .stream()
                            .map(AnnotationTaskState::valueOf)
                            .collect(Collectors.toList())));
          }

          if (request.getAnnotationTypes() != null && request.getAnnotationTypes().size() > 0) {
            predicates.add(
                criteriaBuilder
                    .in(root.get("annotationType"))
                    .value(
                        request
                            .getAnnotationTypes()
                            .stream()
                            .map(AnnotationTypeEnum::getByValue)
                            .collect(Collectors.toList())));
          }

          if (StringUtils.isNotBlank(request.getText())) {
            if (request.getRegexMode() == null || !request.getRegexMode()) {
              predicates.add(
                  criteriaBuilder.like(
                      root.get("text"), String.format("%%%s%%", request.getText())));
            } else {
              predicates.add(
                  criteriaBuilder.equal(
                      criteriaBuilder.function(
                          "rlike",
                          Integer.class,
                          root.get("text"),
                          criteriaBuilder.literal(
                              Pattern.compile(".*" + request.getText() + ".*").toString())),
                      1));
            }
          }

          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
  }

  @Override
  protected void validateRequest(ListAnnotationTaskBlockRequest request)
      throws InvalidInputException {
    if (request == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }

    if (request.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }

    if (request.getPageSize() <= 0 || request.getPageSize() > 50) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
  }

  @Override
  protected PageVO<AnnotationTaskBlockResponse> doBiz(
      ListAnnotationTaskBlockRequest request, UserDetails user) {
    return new PageVO<>(
        annotationTaskBlockRepository
            .findAll(
                queryAnnotationTaskBlockCondition(request),
                PageRequest.of(request.getPageIndex() - 1, request.getPageSize()))
            .map(AnnotationTaskBlockResponse::new));
  }
}
