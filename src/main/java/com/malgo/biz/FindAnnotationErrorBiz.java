package com.malgo.biz;

import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.dto.AnnotationWordError;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.enums.AnnotationRoleStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.GetAnnotationErrorRequest;
import com.malgo.service.AnnotationFactory;
import com.malgo.service.FindAnnotationErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FindAnnotationErrorBiz
    extends BaseBiz<GetAnnotationErrorRequest, List<AnnotationWordError>> {
  private final AnnotationFactory annotationFactory;
  private final FindAnnotationErrorService findAnnotationErrorService;
  private final AnnotationCombineRepository annotationCombineRepository;

  @Autowired
  public FindAnnotationErrorBiz(
      AnnotationFactory annotationFactory,
      AnnotationCombineRepository annotationCombineRepository,
      FindAnnotationErrorService findAnnotationErrorService) {
    this.annotationFactory = annotationFactory;
    this.annotationCombineRepository = annotationCombineRepository;
    this.findAnnotationErrorService = findAnnotationErrorService;
  }

  @Override
  protected void validateRequest(GetAnnotationErrorRequest request) throws InvalidInputException {
    if (request.getAnnotationType() != 0) {
      throw new InvalidInputException("invalid-annotation-type", "目前仅支持分词纠错");
    }

    if (request.getStartId() >= request.getEndId()) {
      throw new InvalidInputException("invalid-start-end-id", "startId必须小于endId");
    }
  }

  @Override
  protected void authorize(int userId, int role, GetAnnotationErrorRequest request)
      throws BusinessRuleException {
    if (role != AnnotationRoleStateEnum.admin.getRole()) {
      throw new BusinessRuleException("permission-denied", "仅管理员可以操作");
    }
  }

  @Override
  protected List<AnnotationWordError> doBiz(GetAnnotationErrorRequest request) {
    final List<AnnotationCombine> annotations =
        annotationCombineRepository.findByAnnotationTypeAndIdBetweenAndStateIn(
            request.getAnnotationType(),
            request.getStartId(),
            request.getEndId(),
            Arrays.asList(
                AnnotationCombineStateEnum.preExamine.name(),
                AnnotationCombineStateEnum.errorPass.name(),
                AnnotationCombineStateEnum.examinePass.name()));
    log.info("find annotation errors, get back {} annotations", annotations.size());

    if (annotations.size() == 0) {
      return Collections.emptyList();
    }

    final List<FindAnnotationErrorService.AlgorithmAnnotationWordError> errors =
        this.findAnnotationErrorService.findErrors(
            annotations.stream().map(this.annotationFactory::create).collect(Collectors.toList()));

    if (errors.size() == 0) {
      return Collections.emptyList();
    }

    log.info("get error result from service: {}", errors.size());

    final List<AnnotationWordError> result =
        errors
            .stream()
            .map(AnnotationWordError::new)
            .sorted((lhs, rhs) -> lhs.getWord().compareToIgnoreCase(rhs.getWord()))
            .collect(Collectors.toList());

    log.info("find annotation errors result, get {} errors", result.size());

    return result;
  }
}
