package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationStaffEvaluateRepository;
import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import cn.malgo.annotation.vo.AnnotationStaffEvaluateVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequirePermission(Permissions.ADMIN)
public class AnnotationEstimateQueryBiz
    extends BaseBiz<AnnotationEstimateQueryRequest, AnnotationStaffEvaluateVO> {

  private final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository;
  private final UserAccountRepository userAccountRepository;

  public AnnotationEstimateQueryBiz(
      UserAccountRepository userAccountRepository,
      AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository) {
    this.annotationStaffEvaluateRepository = annotationStaffEvaluateRepository;
    this.userAccountRepository = userAccountRepository;
  }

  private static Specification<AnnotationStaffEvaluate> queryAnnotationStaffEvaluateCondition(
      AnnotationEstimateQueryRequest param) {
    return (Specification<AnnotationStaffEvaluate>)
        (root, criteriaQuery, criteriaBuilder) -> {
          final List<Predicate> predicates = new ArrayList<>();

          if (param.getWorkDay() != null) {
            predicates.add(criteriaBuilder.equal(root.get("workDay"), param.getWorkDay()));
          }

          if (param.getAssignee() > 0) {
            predicates.add(criteriaBuilder.in(root.get("assignee")).value(param.getAssignee()));
          }

          if (param.getTaskId() > 0) {
            predicates.add(criteriaBuilder.in(root.get("taskId")).value(param.getTaskId()));
          }

          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
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
    if (page.getContent().size() == 0 && annotationEstimateQueryRequest.getWorkDay() != null) {
      try {
        annotationEstimateQueryRequest.setWorkDay(
            new Date(new SimpleDateFormat("yyyy-MM-dd").parse("1000-01-01").getTime()));
      } catch (ParseException e) {
        log.info("time parse exception,{}", e.getMessage());
      }
      page =
          annotationStaffEvaluateRepository.findAll(
              queryAnnotationStaffEvaluateCondition(annotationEstimateQueryRequest),
              PageRequest.of(pageIndex, annotationEstimateQueryRequest.getPageSize()));
    }
    final PageVO<AnnotationEstimateVO> pageVO = new PageVO<>(page.getTotalElements());
    final List<AnnotationStaffEvaluate> annotationStaffEvaluates = page.getContent();
    CurrentTaskOverviewPair currentTaskOverviewPair = null;
    if (annotationStaffEvaluates.size() > 0) {
      Map<Long, String> userMap =
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
                          annotationStaffEvaluate.getInConformity()))
              .collect(Collectors.toList()));
      // 当前批次的总条数，总字数，批次的不一致率
      currentTaskOverviewPair =
          new CurrentTaskOverviewPair(
              annotationStaffEvaluates
                  .stream()
                  .mapToInt(AnnotationStaffEvaluate::getTotalBranchNum)
                  .sum(),
              annotationStaffEvaluates
                  .stream()
                  .mapToInt(AnnotationStaffEvaluate::getTotalWordNum)
                  .sum(),
              annotationStaffEvaluates
                  .stream()
                  .mapToDouble(AnnotationStaffEvaluate::getInConformity)
                  .sum());
    }
    return new AnnotationStaffEvaluateVO(pageVO, currentTaskOverviewPair);
  }

  @Value
  public static class CurrentTaskOverviewPair {
    private final int taskTotalBranch;
    private final int taskTotalWordNum;
    private final double taskInConformity;
  }
}
