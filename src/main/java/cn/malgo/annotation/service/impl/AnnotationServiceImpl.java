package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.config.PermissionConstant;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.User;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.anno.DesignateAnnotationRequest;
import cn.malgo.annotation.request.anno.ListAnnotationRequest;
import cn.malgo.annotation.request.OneKeyDesignateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationService;
import cn.malgo.annotation.service.AnnotationSummaryService;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.model.UserDetails;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnotationServiceImpl implements AnnotationService {

  private final AnnotationRepository annotationRepository;
  private final UserCenterServiceImpl userCenterService;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;
  private final CheckRelationEntityService checkRelationEntityService;
  private final AnnotationFactory annotationFactory;
  private final AnnotationBlockService annotationBlockService;
  private final AnnotationSummaryService annotationSummaryService;

  @Autowired
  public AnnotationServiceImpl(
      final UserCenterServiceImpl userCenterService,
      final AnnotationRepository annotationRepository,
      final ExtractAddAtomicTermService extractAddAtomicTermService,
      final CheckRelationEntityService checkRelationEntityService,
      final AnnotationFactory annotationFactory,
      final AnnotationBlockService annotationBlockService,
      final AnnotationSummaryService annotationSummaryService) {
    this.annotationRepository = annotationRepository;
    this.userCenterService = userCenterService;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
    this.checkRelationEntityService = checkRelationEntityService;
    this.annotationFactory = annotationFactory;
    this.annotationBlockService = annotationBlockService;
    this.annotationSummaryService = annotationSummaryService;
  }

  /** spring-boot-jpa ??????????????? */
  private static Specification<AnnotationNew> queryAnnotationCondition(
      ListAnnotationRequest param) {
    return (Specification<AnnotationNew>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();

          predicates.add(criteriaBuilder.equal(root.get("deleteToken"), 0));

          if (param.getIdList() != null && param.getIdList().size() > 0) {
            predicates.add(criteriaBuilder.in(root.get("id")).value(param.getIdList()));
          }

          if (param.getBlockIds() != null && param.getBlockIds().size() > 0) {
            predicates.add(criteriaBuilder.in(root.get("blockId")).value(param.getBlockIds()));
          }

          if (StringUtils.isNotBlank(param.getTerm())) {
            predicates.add(criteriaBuilder.like(root.get("term"), "%" + param.getTerm() + "%"));
          }

          if (param.getAnnotationTypes() != null && param.getAnnotationTypes().size() > 0) {
            predicates.add(
                criteriaBuilder
                    .in(root.get("annotationType"))
                    .value(
                        param
                            .getAnnotationTypes()
                            .stream()
                            .map(AnnotationTypeEnum::getByValue)
                            .collect(Collectors.toList())));
          }

          if (param.getStates() != null && param.getStates().size() > 0) {
            final List<AnnotationStateEnum> states =
                param
                    .getStates()
                    .stream()
                    .map(
                        state -> {
                          try {
                            return AnnotationStateEnum.valueOf(state);
                          } catch (IllegalArgumentException ex) {
                            log.warn("wrong state passed: {}", state);
                            return null;
                          }
                        })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            predicates.add(criteriaBuilder.in(root.get("state")).value(states));
          }

          if (param.getUserId() > 0) {
            predicates.add(criteriaBuilder.equal(root.get("assignee"), param.getUserId()));
          }

          if (param.getLeftDate() != null) {
            predicates.add(
                criteriaBuilder.greaterThanOrEqualTo(root.get("createdTime"), param.getLeftDate()));
          }

          if (param.getRightDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(param.getRightDate());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            predicates.add(
                criteriaBuilder.lessThanOrEqualTo(root.get("createdTime"), calendar.getTime()));
          }

          return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
  }

  /** ???????????????????????? */
  @Override
  public Page<AnnotationNew> listAnnotationNew(ListAnnotationRequest request) {
    Page<AnnotationNew> page =
        annotationRepository.findAll(
            queryAnnotationCondition(request),
            PageRequest.of(
                request.getPageIndex(), request.getPageSize(), Direction.DESC, "createdTime"));

    if (page.getTotalElements() > 0) {
      final Map<Long, String> userMap =
          userCenterService
              .getUsersByUserCenter()
              .stream()
              .collect(Collectors.toMap(User::getUserId, User::getNickName));

      page.getContent()
          .forEach(
              annotationNew ->
                  annotationNew.setUserName(userMap.getOrDefault(annotationNew.getAssignee(), "")));
    }

    return page;
  }

  /** ??????????????????????????????????????? */
  @Override
  public void designateAnnotationNew(DesignateAnnotationRequest request) {
    final List<AnnotationNew> annotationNews =
        annotationRepository.findAllById(request.getIdList());

    annotationNews.forEach(
        annotationNew -> {
          annotationNew.setAssignee(request.getUserId());
          annotationNew.setState(AnnotationStateEnum.PRE_ANNOTATION);
          annotationNew.setExpirationTime(getExpirationTime().getTime());
        });
    annotationRepository.saveAll(annotationNews);
  }

  private Calendar getExpirationTime() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DAY_OF_MONTH, 2);
    return calendar;
  }

  @Override
  public void oneKeyDesignateAnnotationNew(OneKeyDesignateAnnotationRequest request) {
    final List<AnnotationNew> annotationNews =
        annotationRepository.findAllByStateInAndAnnotationTypeIn(
            Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED),
            request
                .getAnnotationTypes()
                .parallelStream()
                .map(AnnotationTypeEnum::valueOf)
                .collect(Collectors.toList()),
            Sort.by(Direction.DESC, "createdTime"));
    if (annotationNews.size() > 0) {
      if (request.getDesignateWordNum()
          > annotationNews
              .stream()
              .mapToInt(annotationNew -> annotationNew.getTerm().length())
              .sum()) {
        throw new BusinessRuleException(
            "invalid-designate-word-num", "????????????designateWordNum???????????????????????????");
      }
      final List<AnnotationNew> resultAnnotationNews = new ArrayList<>();
      long wordSum = request.getDesignateWordNum();
      for (int k = 0; k < annotationNews.size(); k++) {
        final AnnotationNew current = annotationNews.get(k);
        resultAnnotationNews.add(current);
        wordSum -= current.getTerm().length();
        if (wordSum <= 0) {
          break;
        }
      }
      final List<Long> userIdList = request.getUserIdList();
      IntStream.range(0, resultAnnotationNews.size())
          .forEach(
              i -> {
                int k = i % userIdList.size();
                resultAnnotationNews.get(i).setAssignee(userIdList.get(k));
                resultAnnotationNews.get(i).setState(AnnotationStateEnum.PRE_ANNOTATION);
                resultAnnotationNews.get(i).setExpirationTime(getExpirationTime().getTime());
              });
      annotationRepository.saveAll(resultAnnotationNews);
    }
  }

  @Override
  public void annotationSingleCommit(UserDetails user, AnnotationNew annotationNew) {
    final Annotation annotation = annotationFactory.create(annotationNew);

    if (annotationNew.getAnnotationType() == AnnotationTypeEnum.relation
        && checkRelationEntityService.hasIsolatedAnchor(annotation)) {
      throw new BusinessRuleException("has-isolated-anchor-type", "????????????????????????????????????");
    }

    switch (annotationNew.getState()) {
      case PRE_ANNOTATION:
      case ANNOTATION_PROCESSING:
        if (!user.hasPermission(PermissionConstant.ANNOTATION_TASK_DESIGNATE)) {
          if (annotationNew.getAssignee() != user.getId()) {
            throw new BusinessRuleException("permission-denied", "?????????????????????????????????????????????");
          }
        }
        break;
      default:
        throw new BusinessRuleException("invalid-state", "?????????????????????????????????");
    }

    annotationNew.setState(AnnotationStateEnum.SUBMITTED);
    annotationNew.setCommitTimestamp(new Date());
    if (annotationNew.getAnnotationType() == AnnotationTypeEnum.wordPos
        || annotationNew.getAnnotationType() == AnnotationTypeEnum.disease) { // ?????????????????????????????????
      extractAddAtomicTermService.extractAndAddAtomicTerm(annotation);
    }
    annotationBlockService.saveAnnotation(annotationRepository.save(annotationNew));
    annotationSummaryService.updateTaskSummary(annotationNew.getTaskId());
  }
}
