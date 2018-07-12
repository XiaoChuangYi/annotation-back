package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.error.AnnotationErrorContext;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.SearchAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequireRole(AnnotationRoleStateEnum.admin)
public class SearchAnnotationBiz
    extends BaseBiz<SearchAnnotationRequest, List<AnnotationErrorContext>> {
  private final AnnotationFactory annotationFactory;
  private final AnnotationTaskBlockRepository blockRepository;

  @Autowired
  public SearchAnnotationBiz(
      final AnnotationFactory annotationFactory,
      final AnnotationTaskBlockRepository blockRepository) {
    this.annotationFactory = annotationFactory;
    this.blockRepository = blockRepository;
  }

  @Override
  protected void validateRequest(SearchAnnotationRequest request) throws InvalidInputException {
    if (request.getAnnotationType() != 0) {
      throw new InvalidInputException("invalid-annotation-type", "目前仅支持分词纠错");
    }

    if (StringUtils.isBlank(request.getTerm())) {
      throw new InvalidInputException("invalid-search-term", "term必须有值");
    }
  }

  @Override
  protected List<AnnotationErrorContext> doBiz(SearchAnnotationRequest request) {
    final List<AnnotationTaskBlock> annotations =
        blockRepository.findByAnnotationTypeEqualsAndStateInAndTaskDocs_TaskDoc_Task_IdEquals(
            AnnotationTypeEnum.getByValue(request.getAnnotationType()),
            Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.FINISHED),
            request.getTaskId());
    log.info("search annotations, get back {} annotations", annotations.size());

    if (annotations.size() == 0) {
      return Collections.emptyList();
    }

    final Predicate<String> term = Pattern.compile(request.getTerm()).asPredicate();
    final Predicate<String> type =
        StringUtils.isBlank(request.getType())
            ? (s) -> true
            : Pattern.compile(request.getType()).asPredicate();

    final List<AnnotationErrorContext> results =
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
                            entity -> term.test(entity.getTerm()) && type.test(entity.getType()))
                        .map(
                            entity ->
                                new AnnotationErrorContext(
                                    annotation,
                                    new BratPosition(entity.getStart(), entity.getEnd()),
                                    null)))
            .collect(Collectors.toList());

    log.info("search annotations results {}", results.size());
    return results;
  }
}
