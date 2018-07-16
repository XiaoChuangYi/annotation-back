package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.task.ListAnnotationTaskRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationTaskVO;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
@Slf4j
public class ListAnnotationTaskBiz
    extends BaseBiz<ListAnnotationTaskRequest, PageVO<AnnotationTaskVO>> {

  private final AnnotationTaskRepository annotationTaskRepository;
  private final AnnotationTaskDocRepository annotationTaskDocRepository;

  public ListAnnotationTaskBiz(
      AnnotationTaskRepository annotationTaskRepository,
      AnnotationTaskDocRepository annotationTaskDocRepository) {
    this.annotationTaskRepository = annotationTaskRepository;
    this.annotationTaskDocRepository = annotationTaskDocRepository;
  }

  private static Specification<AnnotationTask> queryAnnotationTaskCondition(
      ListAnnotationTaskRequest param) {
    return (Specification<AnnotationTask>)
        (root, criteriaQuery, criteriaBuilder) -> {
          // todo 还会有其它的过滤条件
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
          return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
  }

  @Override
  protected void validateRequest(ListAnnotationTaskRequest listAnnotationTaskRequest)
      throws InvalidInputException {
    if (listAnnotationTaskRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (listAnnotationTaskRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }
    if (listAnnotationTaskRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
    // todo ,其它过滤条件的校验暂定
  }

  @Override
  protected PageVO<AnnotationTaskVO> doBiz(
      int userId, int role, ListAnnotationTaskRequest listAnnotationTaskRequest) {
    final int pageIndex = listAnnotationTaskRequest.getPageIndex() - 1;
    final Page<AnnotationTask> page =
        annotationTaskRepository.findAll(
            queryAnnotationTaskCondition(listAnnotationTaskRequest),
            PageRequest.of(pageIndex, listAnnotationTaskRequest.getPageSize()));
    final PageVO<AnnotationTaskVO> pageVO = new PageVO(page, false);
    final List<AnnotationTaskVO> annotationTaskVOList =
        page.getContent()
            .stream()
            .map(
                annotationTask ->
                    new AnnotationTaskVO(
                        annotationTask.getId(),
                        annotationTask.getCreatedTime(),
                        annotationTask.getLastModified(),
                        annotationTask.getName(),
                        annotationTask.getState().name()))
            .collect(Collectors.toList());
    pageVO.setDataList(annotationTaskVOList);
    return pageVO;
  }
}
