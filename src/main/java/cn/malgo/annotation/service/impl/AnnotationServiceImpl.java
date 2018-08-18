package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.request.ListAnnotationRequest;
import cn.malgo.annotation.request.OneKeyDesignateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationService;
import cn.malgo.service.exception.BusinessRuleException;
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
  private final UserAccountRepository userAccountRepository;

  @Autowired
  public AnnotationServiceImpl(
      final UserAccountRepository userAccountRepository,
      final AnnotationRepository annotationRepository) {
    this.userAccountRepository = userAccountRepository;
    this.annotationRepository = annotationRepository;
  }

  /** spring-boot-jpa 自定义查询 */
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

  /** 条件查询标注任务 */
  @Override
  public Page<AnnotationNew> listAnnotationNew(ListAnnotationRequest request) {
    Page<AnnotationNew> page =
        annotationRepository.findAll(
            queryAnnotationCondition(request),
            PageRequest.of(
                request.getPageIndex(), request.getPageSize(), Direction.DESC, "createdTime"));

    if (page.getTotalElements() > 0) {
      final Map<Long, String> userMap =
          userAccountRepository
              .findAll()
              .stream()
              .collect(Collectors.toMap(UserAccount::getId, UserAccount::getAccountName));

      page.getContent()
          .forEach(
              annotationNew ->
                  annotationNew.setUserName(userMap.getOrDefault(annotationNew.getAssignee(), "")));
    }

    return page;
  }

  /** 批量指派标注数据给特定用户 */
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
        annotationRepository.findAllByStateIn(
            Collections.singletonList(AnnotationStateEnum.UN_DISTRIBUTED),
            Sort.by(Direction.DESC, "createdTime"));
    if (annotationNews.size() > 0) {
      if (request.getDesignateWordNum()
          > annotationNews
              .stream()
              .mapToInt(annotationNew -> annotationNew.getTerm().length())
              .sum()) {
        throw new BusinessRuleException(
            "invalid-designate-word-num", "请求参数designateWordNum超过可分配的最大值");
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
              });
      annotationRepository.saveAll(resultAnnotationNews);
    }
  }
}
