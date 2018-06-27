package cn.malgo.annotation.biz;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AnnotationErrorContext;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.SearchAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.FindAnnotationErrorService;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SearchAnnotationBiz
    extends BaseBiz<SearchAnnotationRequest, List<AnnotationErrorContext>> {
  private final AnnotationFactory annotationFactory;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final FindAnnotationErrorService findAnnotationErrorService;

  @Autowired
  public SearchAnnotationBiz(
      final AnnotationFactory annotationFactory,
      final AnnotationCombineRepository annotationCombineRepository,
      final FindAnnotationErrorService findAnnotationErrorService) {
    this.annotationFactory = annotationFactory;
    this.annotationCombineRepository = annotationCombineRepository;
    this.findAnnotationErrorService = findAnnotationErrorService;
  }

  @Override
  protected void validateRequest(SearchAnnotationRequest request) throws InvalidInputException {
    if (request.getAnnotationType() != 0) {
      throw new InvalidInputException("invalid-annotation-type", "目前仅支持分词纠错");
    }

    if (request.getStartId() >= request.getEndId()) {
      throw new InvalidInputException("invalid-start-end-id", "startId必须小于endId");
    }

    if (StringUtils.isBlank(request.getTerm())) {
      throw new InvalidInputException("invalid-search-term", "term必须有值");
    }
  }

  @Override
  protected void authorize(int userId, int role, SearchAnnotationRequest request)
      throws BusinessRuleException {
    if (role != AnnotationRoleStateEnum.admin.getRole()) {
      throw new BusinessRuleException("permission-denied", "仅管理员可以操作");
    }
  }

  @Override
  protected List<AnnotationErrorContext> doBiz(SearchAnnotationRequest request) {
    final List<AnnotationCombine> annotations =
        annotationCombineRepository.findByAnnotationTypeAndIdBetweenAndStateIn(
            request.getAnnotationType(),
            request.getStartId(),
            request.getEndId(),
            Arrays.asList(
                AnnotationCombineStateEnum.preExamine.name(),
                AnnotationCombineStateEnum.errorPass.name(),
                AnnotationCombineStateEnum.examinePass.name()));
    log.info("search annotations, get back {} annotations", annotations.size());

    if (annotations.size() == 0) {
      return Collections.emptyList();
    }

    final Pattern term = Pattern.compile(request.getTerm());
    final Pattern type =
        StringUtils.isBlank(request.getType()) ? null : Pattern.compile(request.getType());

    List<AnnotationErrorContext> results =
        annotations
            .stream()
            .map(this.annotationFactory::create)
            .flatMap(
                annotation ->
                    annotation
                        .getDocument()
                        .getEntities()
                        .stream()
                        .filter(
                            entity ->
                                term.matcher(entity.getTerm()).matches()
                                    && (type == null || type.matcher(entity.getType()).matches()))
                        .map(
                            entity ->
                                new AnnotationErrorContext(
                                    annotation,
                                    new BratPosition(entity.getStart(), entity.getEnd()))))
            .collect(Collectors.toList());
    log.info(
        "search annotations potential results {}, will filter: {}",
        results.size(),
        request.isFilterFixedErrors());

    if (request.isFilterFixedErrors()) {
      results = this.findAnnotationErrorService.filterErrors(results).collect(Collectors.toList());
      log.info("search annotations filtered results {}", results.size());
    }

    return results;
  }
}
