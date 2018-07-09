package cn.malgo.annotation.biz;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.AnnotationWordError;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.FindAnnotationErrorRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.FindAnnotationErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequireRole(AnnotationRoleStateEnum.admin)
public class FindAnnotationErrorBiz
    extends BaseBiz<FindAnnotationErrorRequest, List<AnnotationWordError>> {
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
  protected void validateRequest(FindAnnotationErrorRequest request) throws InvalidInputException {
    if (request.getErrorType() < 0
        || request.getErrorType() >= AnnotationErrorEnum.values().length) {
      throw new InvalidInputException(
          "invalid-annotation-type", request.getErrorType() + "不是合法的错误类型");
    }

    if (request.getStartId() >= request.getEndId()) {
      throw new InvalidInputException("invalid-start-end-id", "startId必须小于endId");
    }
  }

  @Override
  protected List<AnnotationWordError> doBiz(FindAnnotationErrorRequest request) {
    final AnnotationErrorEnum errorType = AnnotationErrorEnum.values()[request.getErrorType()];

    final List<AnnotationCombine> annotationCombines =
        annotationCombineRepository.findByAnnotationTypeAndIdBetweenAndStateIn(
            errorType.getAnnotationType().ordinal(),
            request.getStartId(),
            request.getEndId(),
            Arrays.asList(
                AnnotationCombineStateEnum.preExamine.name(),
                AnnotationCombineStateEnum.errorPass.name(),
                AnnotationCombineStateEnum.examinePass.name()));
    log.info("find annotation errors, get back {} annotations", annotationCombines.size());

    if (annotationCombines.size() == 0) {
      return Collections.emptyList();
    }

    final List<Annotation> annotations =
        annotationCombines
            .stream()
            .map(this.annotationFactory::create)
            .collect(Collectors.toList());

    final List<FindAnnotationErrorService.AlgorithmAnnotationWordError> errors =
        findAnnotationErrorService.findErrors(errorType, annotations);

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
