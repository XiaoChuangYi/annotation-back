package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.AnnotationWordError;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
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
  private final AnnotationTaskBlockRepository blockRepository;

  @Autowired
  public FindAnnotationErrorBiz(
      final AnnotationFactory annotationFactory,
      final FindAnnotationErrorService findAnnotationErrorService,
      final AnnotationTaskBlockRepository blockRepository) {
    this.annotationFactory = annotationFactory;
    this.blockRepository = blockRepository;
    this.findAnnotationErrorService = findAnnotationErrorService;
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

    final List<AnnotationTaskBlock> blocks =
        blockRepository.findByAnnotationTypeEqualsAndStateInAndTaskDocs_TaskDoc_Task_IdEquals(
            errorType.getAnnotationType(),
            Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.FINISHED),
            request.getTaskId());
    log.info("find annotation errors, get back {} annotations", blocks.size());

    if (blocks.size() == 0) {
      return Collections.emptyList();
    }

    final List<Annotation> annotations =
        blocks.stream().map(this.annotationFactory::create).collect(Collectors.toList());

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
