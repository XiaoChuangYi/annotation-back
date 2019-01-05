package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.User;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.TaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.task.ListAnnotationTaskBlockRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.service.UserCenterService;
import cn.malgo.annotation.vo.AnnotationTaskBlockResponse;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ListAnnotationTaskBlockBiz
    extends BaseBiz<ListAnnotationTaskBlockRequest, PageVO<AnnotationTaskBlockResponse>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final UserCenterService userCenterService;

  public ListAnnotationTaskBlockBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final UserCenterService userCenterService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.userCenterService = userCenterService;
  }

  private Specification<AnnotationTaskBlock> queryAnnotationTaskBlockCondition(
      ListAnnotationTaskBlockRequest request) {
    return (Specification<AnnotationTaskBlock>)
        (root, criteriaQuery, criteriaBuilder) -> {
          final List<Predicate> predicates = new ArrayList<>();

          if (request.getTaskId() != 0) {
            Join<AnnotationTask, TaskBlock> joinA = root.join("taskBlocks", JoinType.LEFT);
            predicates.add(criteriaBuilder.equal(joinA.get("task").get("id"), request.getTaskId()));
          }

          if (request.getId() != null && request.getId() != 0) {
            predicates.add(criteriaBuilder.equal(root.get("id"), request.getId()));
          }

          if (request.getStates() != null && request.getStates().size() > 0) {
            final List<AnnotationTaskState> states =
                request
                    .getStates()
                    .stream()
                    .map(AnnotationTaskState::valueOf)
                    .collect(Collectors.toList());

            predicates.add(criteriaBuilder.in(root.get("state")).value(states));
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
    if (request.getTaskId() < 0) {
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
  protected PageVO<AnnotationTaskBlockResponse> doBiz(
      ListAnnotationTaskBlockRequest request, UserDetails user) {
    PageRequest page = PageRequest.of(request.getPageIndex() - 1, request.getPageSize());

    if (request.getStates() != null
        && request.getStates().size() == 1
        && request.getStates().get(0).equals(AnnotationTaskState.CREATED.name())) {
      page =
          PageRequest.of(
              request.getPageIndex() - 1,
              request.getPageSize(),
              Sort.by(Direction.DESC, "nerFreshRate"));
    }
    //    final Map<Long, String> longStringMap =
    //        userCenterService
    //            .getUsersByUserCenter()
    //            .parallelStream()
    //            .collect(Collectors.toMap(User::getUserId, User::getNickName));
    return new PageVO<>(
        annotationTaskBlockRepository
            .findAll(queryAnnotationTaskBlockCondition(request), page)
            .map(
                annotationTaskBlock ->
                    new AnnotationTaskBlockResponse(
                        annotationTaskBlock, ""
                        //
                        // longStringMap.getOrDefault(annotationTaskBlock.getAssignee(), "")
                        )));
  }
}
