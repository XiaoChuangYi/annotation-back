package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.UserAccountRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.UserAccount;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.DesignateAnnotationRequest;
import cn.malgo.annotation.request.ListAnnotationCombineRequest;
import cn.malgo.annotation.request.RandomDesignateAnnotationRequest;
import cn.malgo.annotation.service.AnnotationCombineService;
import java.util.Calendar;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class AnnotationCombineServiceImpl implements AnnotationCombineService {
  private final AnnotationCombineRepository annotationCombineRepository;
  private final UserAccountRepository userAccountRepository;

  @Autowired
  public AnnotationCombineServiceImpl(
      AnnotationCombineRepository annotationCombineRepository,
      UserAccountRepository userAccountRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.userAccountRepository = userAccountRepository;
  }

  /** spring-boot-jpa 自定义查询 */
  private static Specification<AnnotationCombine> queryAnnotationCombineCondition(
      ListAnnotationCombineRequest param) {
    return (Specification<AnnotationCombine>)
        (root, criteriaQuery, criteriaBuilder) -> {
          List<Predicate> predicates = new ArrayList<>();
          //          predicates.add(criteriaBuilder.equal(root.get("isTask"), 0));
          if (param.getIdList() != null && param.getIdList().size() > 0) {
            predicates.add(criteriaBuilder.in(root.get("id")).value(param.getIdList()));
          }
          if (StringUtils.isNotBlank(param.getTerm())) {
            predicates.add(criteriaBuilder.like(root.get("term"), "%" + param.getTerm() + "%"));
          }
          if (param.getAnnotationTypes() != null && param.getAnnotationTypes().size() > 0) {
            predicates.add(
                criteriaBuilder.in(root.get("annotationType")).value(param.getAnnotationTypes()));
          }
          if (param.getStates() != null && param.getStates().size() > 0) {
            predicates.add(criteriaBuilder.in(root.get("state")).value(param.getStates()));
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
  public Page<AnnotationCombine> listAnnotationCombine(
      ListAnnotationCombineRequest listAnnotationCombineRequest) {
    Page<AnnotationCombine> page =
        annotationCombineRepository.findAll(
            queryAnnotationCombineCondition(listAnnotationCombineRequest),
            PageRequest.of(
                listAnnotationCombineRequest.getPageIndex(),
                listAnnotationCombineRequest.getPageSize(),
                Direction.DESC,
                "createdTime"));
    Map<Long, String> userMap;
    if (page.getTotalElements() > 0) {
      userMap =
          userAccountRepository
              .findAll()
              .stream()
              .collect(Collectors.toMap(UserAccount::getId, UserAccount::getAccountName));
      page.getContent()
          .forEach(
              annotationCombine ->
                  annotationCombine.setUserName(
                      userMap.getOrDefault(annotationCombine.getAssignee(), "")));
    }
    return page;
  }

  /** 批量指派标注数据给特定用户 */
  @Override
  public void designateAnnotationCombine(DesignateAnnotationRequest request) {
    final List<AnnotationCombine> annotationCombineList =
        annotationCombineRepository.findAllById(request.getIdList());
    annotationCombineList.forEach(
        annotationCombine -> {
          annotationCombine.setAssignee(request.getUserId());
          annotationCombine.setState(AnnotationCombineStateEnum.preAnnotation.name());
          annotationCombine.setReviewedAnnotation("");
        });
    annotationCombineRepository.saveAll(annotationCombineList);
  }

  /** 随机批量指派标注数据给用户 */
  @Override
  public void randomDesignateAnnotationCombine(
      RandomDesignateAnnotationRequest randomDesignateAnnotationRequest) {
    // 第一步根据未分配状态，标注类型，以及num，查询出所有的标注
    List<AnnotationCombine> annotationCombineList =
        annotationCombineRepository.findAllByAnnotationTypeInAndStateEquals(
            randomDesignateAnnotationRequest.getAnnotationTypes(),
            AnnotationCombineStateEnum.unDistributed.name(),
            PageRequest.of(0, randomDesignateAnnotationRequest.getNum()));

    // 第二步(假)随机更新对应的标注的assignee
    List<Integer> userIdList = randomDesignateAnnotationRequest.getUserIdList();
    IntStream.range(0, annotationCombineList.size())
        .forEach(
            i -> {
              int k = i % userIdList.size();
              annotationCombineList.get(i).setAssignee(userIdList.get(k));
              annotationCombineList
                  .get(i)
                  .setState(AnnotationCombineStateEnum.preAnnotation.name());
              annotationCombineList.get(i).setReviewedAnnotation("");
            });
    annotationCombineRepository.saveAll(annotationCombineList);
  }
}
