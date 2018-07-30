package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.AnnotationWordError;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.FindAnnotationErrorRequest;
import cn.malgo.annotation.service.AnnotationErrorFactory;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequirePermission(Permissions.ADMIN)
public class FindAnnotationErrorBiz
    extends BaseBiz<FindAnnotationErrorRequest, List<AnnotationWordError>> {

  private final AnnotationFactory annotationFactory;
  private final AnnotationErrorFactory annotationErrorFactory;
  private final AnnotationTaskBlockRepository blockRepository;

  @Autowired
  public FindAnnotationErrorBiz(
      final AnnotationFactory annotationFactory,
      final AnnotationTaskBlockRepository blockRepository,
      final AnnotationErrorFactory annotationErrorFactory) {
    this.annotationFactory = annotationFactory;
    this.blockRepository = blockRepository;
    this.annotationErrorFactory = annotationErrorFactory;
  }

  @Override
  protected void validateRequest(FindAnnotationErrorRequest request) throws InvalidInputException {
    if (request.getErrorType() < 0
        || request.getErrorType() >= AnnotationErrorEnum.values().length) {
      throw new InvalidInputException(
          "invalid-annotation-type", request.getErrorType() + "不是合法的错误类型");
    }
  }

  @Override
  protected List<AnnotationWordError> doBiz(FindAnnotationErrorRequest request) {
    final AnnotationErrorEnum errorType = AnnotationErrorEnum.values()[request.getErrorType()];

    final Set<AnnotationTaskBlock> blocks =
        blockRepository.findByAnnotationTypeEqualsAndStateIn(
            errorType.getAnnotationType(),
            Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.FINISHED));
    log.info("find annotation errors, get back {} annotations", blocks.size());

    if (blocks.size() == 0) {
      return Collections.emptyList();
    }

    final List<Annotation> annotations =
        blocks.stream().map(this.annotationFactory::create).collect(Collectors.toList());

    final List<AlgorithmAnnotationWordError> errors =
        annotationErrorFactory.getProvider(errorType).find(annotations);

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
