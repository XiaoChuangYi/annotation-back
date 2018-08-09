package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.request.task.ListAnnotationTaskRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequirePermission(Permissions.ADMIN)
@Slf4j
public class ListAnnotationTaskBiz
    extends BaseBiz<ListAnnotationTaskRequest, PageVO<AnnotationTaskVO>> {

  private final AnnotationTaskRepository annotationTaskRepository;

  public ListAnnotationTaskBiz(AnnotationTaskRepository annotationTaskRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
  }

  private static Specification<AnnotationTask> queryAnnotationTaskCondition(
      ListAnnotationTaskRequest param) {
    return (Specification<AnnotationTask>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();

          if (StringUtils.isNotBlank(param.getName())) {
            predicates.add(
                criteriaBuilder.like(
                    root.get("name"), String.format("%s%s%s", "%", param.getName(), "%")));
          }

          if (param.getTaskStates() != null
              && param.getTaskStates().size() > 0
              && !param.getTaskStates().contains(null)) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(param.getTaskStates()));
          }

          if (param.getTaskId() > 0) {
            predicates.add(criteriaBuilder.in(root.get("id")).value(param.getTaskId()));
          }

          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
  }

  @Override
  protected void validateRequest(ListAnnotationTaskRequest listAnnotationTaskRequest)
      throws InvalidInputException {
    if (listAnnotationTaskRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }

    if (listAnnotationTaskRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
  }

  @Override
  protected PageVO<AnnotationTaskVO> doBiz(ListAnnotationTaskRequest request, UserDetails user) {
    if (request.isAll()) {
      final List<AnnotationTask> tasks = annotationTaskRepository.findAll();
      return new PageVO<>(
          tasks.size(), tasks.stream().map(AnnotationTaskVO::new).collect(Collectors.toList()));
    }
    final Page<AnnotationTask> page =
        annotationTaskRepository.findAll(
            queryAnnotationTaskCondition(request),
            PageRequest.of(
                request.getPageIndex() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")));

    return new PageVO<>(
        page.getTotalElements(),
        page.getContent().stream().map(AnnotationTaskVO::new).collect(Collectors.toList()));
  }
}
