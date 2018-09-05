package cn.malgo.annotation.biz;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.AnnotationStaffEvaluateRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.dto.User;
import cn.malgo.annotation.entity.AnnotationStaffEvaluate;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.request.AnnotationEstimateQueryRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.service.UserCenterService;
import cn.malgo.annotation.service.feigns.UserCenterClient;
import cn.malgo.annotation.vo.AnnotationEstimateVO;
import cn.malgo.annotation.vo.AnnotationStaffEvaluateVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.criteria.Predicate;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
  private final UserCenterService userCenterService;
  private final AnnotationTaskRepository taskRepository;
  private final AnnotationRepository annotationRepository;

  public AnnotationEstimateQueryBiz(
      final UserCenterService userCenterService,
      final AnnotationStaffEvaluateRepository annotationStaffEvaluateRepository,
      final AnnotationTaskRepository taskRepository,
      final AnnotationRepository annotationRepository) {
    this.annotationStaffEvaluateRepository = annotationStaffEvaluateRepository;
    this.userCenterService = userCenterService;
    this.taskRepository = taskRepository;
    this.annotationRepository = annotationRepository;
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
  protected void validateRequest(AnnotationEstimateQueryRequest request)
      throws InvalidInputException {
    if (request.getTaskId() <= 0) {
      throw new InvalidInputException("invalid-task-id", "无效的参数taskId");
    }

    if (request.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的参数pageIndex");
    }

    if (request.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的参数pageSize");
    }
  }

  @Override
  public AnnotationStaffEvaluateVO doBiz(AnnotationEstimateQueryRequest request) {
    final int pageIndex = request.getPageIndex() - 1;
    Page<AnnotationStaffEvaluate> page =
        annotationStaffEvaluateRepository.findAll(
            queryAnnotationStaffEvaluateCondition(request),
            PageRequest.of(pageIndex, request.getPageSize()));
    final PageVO<AnnotationEstimateVO> pageVO = new PageVO<>(page.getTotalElements());
    final List<AnnotationStaffEvaluate> annotationStaffEvaluates = page.getContent();
    final AnnotationTask task = taskRepository.getOne(request.getTaskId());
    if (annotationStaffEvaluates.size() > 0) {
      final Map<Long, String> userMap =
          userCenterService
              .getUsersByUserCenter()
              .stream()
              .collect(Collectors.toMap(User::getUserId, User::getNickName));
      pageVO.setDataList(
          annotationStaffEvaluates
              .stream()
              .map(
                  annotationStaffEvaluate ->
                      // 暂定abandonBranchNum 为totalAbandonWordNum;暂定abandonWordNum为 当天放弃字数
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
    final int totalAbandonWordNum =
        annotationRepository
            .findByTaskIdAndStateIn(
                request.getTaskId(),
                Arrays.asList(
                    AnnotationStateEnum.SUBMITTED,
                    AnnotationStateEnum.PRE_CLEAN,
                    AnnotationStateEnum.CLEANED))
            .parallelStream()
            .filter(annotationNew -> StringUtils.isBlank(annotationNew.getFinalAnnotation()))
            .mapToInt(value -> value.getTerm().length())
            .sum();
    return new AnnotationStaffEvaluateVO(
        pageVO,
        new CurrentTaskOverviewPair(
            task.getTotalBranchNum(),
            task.getTotalWordNum(),
            totalAbandonWordNum,
            task.getPrecisionRate(),
            task.getRecallRate()));
  }

  @Value
  public static class CurrentTaskOverviewPair {

    private final int taskTotalBranch;
    private final int taskTotalWordNum;
    private final int taskTotalAbandonWordNum;

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private final Double taskPreciseRate;

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private final Double taskRecallRate;
  }
}
