package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationStaffEvaluateRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import cn.malgo.annotation.vo.AnnotationStaffEvaluateVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequirePermission(Permissions.ADMIN)
public class AnnotationEstimateQueryBiz
    extends BaseBiz<AnnotationEstimateQueryRequest, AnnotationStaffEvaluateVO> {

  private final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository;
  private final UserAccountRepository userAccountRepository;
  private final AnnotationTaskRepository taskRepository;

  public AnnotationEstimateQueryBiz(
      final UserAccountRepository userAccountRepository,
      final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository,
      final AnnotationTaskRepository taskRepository) {
    this.annotationStaffEvaluateRepository = annotationStaffEvaluateRepository;
    this.userAccountRepository = userAccountRepository;
    this.taskRepository = taskRepository;
  }

  private static Specification<AnnotationStaffEvaluate> queryAnnotationStaffEvaluateCondition(
      AnnotationEstimateQueryRequest param) {
    return (Specification<AnnotationStaffEvaluate>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          if (param.getWorkDay() != null) {
            predicates.add(criteriaBuilder.equal(root.get("workDay"), param.getWorkDay()));
          }
          if (param.getAssignee() > 0) {
            predicates.add(criteriaBuilder.in(root.get("assignee")).value(param.getAssignee()));
          }
          if (param.getTaskId() > 0) {
            predicates.add(criteriaBuilder.in(root.get("taskId")).value(param.getTaskId()));
          }
          return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
  }

  @Override
  protected void validateRequest(AnnotationEstimateQueryRequest annotationEstimateQueryRequest)
      throws InvalidInputException {
    if (annotationEstimateQueryRequest.getTaskId() <= 0) {
      throw new InvalidInputException("invalid-task-id", "无效的参数taskId");
    }

    if (annotationEstimateQueryRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }

    if (annotationEstimateQueryRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
  }

  @Override
  public AnnotationStaffEvaluateVO doBiz(
      AnnotationEstimateQueryRequest annotationEstimateQueryRequest) {
    final int pageIndex = annotationEstimateQueryRequest.getPageIndex() - 1;
    Page<AnnotationStaffEvaluate> page =
        annotationStaffEvaluateRepository.findAll(
            queryAnnotationStaffEvaluateCondition(annotationEstimateQueryRequest),
            PageRequest.of(pageIndex, annotationEstimateQueryRequest.getPageSize()));
    final PageVO<AnnotationEstimateVO> pageVO = new PageVO<>(page.getTotalElements());
    final List<AnnotationStaffEvaluate> annotationStaffEvaluates = page.getContent();
    final AnnotationTask task = taskRepository.getOne(annotationEstimateQueryRequest.getTaskId());
    if (annotationStaffEvaluates.size() > 0) {
      final Map<Long, String> userMap =
          userAccountRepository
              .findAll()
              .stream()
              .collect(Collectors.toMap(UserAccount::getId, UserAccount::getAccountName));
      pageVO.setDataList(
          annotationStaffEvaluates
              .stream()
              .map(
                  annotationStaffEvaluate ->
                      new AnnotationEstimateVO(
                          annotationStaffEvaluate.getTaskId(),
                          annotationStaffEvaluate.getAssignee(),
                          userMap.get(annotationStaffEvaluate.getAssignee()),
                          annotationStaffEvaluate.getTaskName(),
                          annotationStaffEvaluate.getWorkDay(),
                          annotationStaffEvaluate.getTotalBranchNum(),
                          annotationStaffEvaluate.getTotalWordNum(),
                          annotationStaffEvaluate.getCurrentDayAnnotatedBranchNum(),
                          annotationStaffEvaluate.getCurrentDayAnnotatedWordNum(),
                          annotationStaffEvaluate.getRestBranchNum(),
                          annotationStaffEvaluate.getRestWordNum(),
                          annotationStaffEvaluate.getAbandonBranchNum(),
                          annotationStaffEvaluate.getAbandonWordNum(),
                          annotationStaffEvaluate.getPrecisionRate(),
                          annotationStaffEvaluate.getRecallRate()))
              .collect(Collectors.toList()));
    }

    return new AnnotationStaffEvaluateVO(
        pageVO,
        new CurrentTaskOverviewPair(
            task.getTotalBranchNum(),
            task.getTotalWordNum(),
            task.getPrecisionRate(),
            task.getRecallRate()));
  }

  @Value
  public static class CurrentTaskOverviewPair {
    private final int taskTotalBranch;
    private final int taskTotalWordNum;
    private final double taskPreciseRate;
    private final double taskRecallRate;
  }
}
