package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.error.AnnotationErrorContext;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.SearchAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.core.definition.brat.BratPosition;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequirePermission(Permissions.ADMIN)
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
    if (StringUtils.isBlank(request.getTerm())) {
      throw new InvalidInputException("invalid-search-term", "term必须有值");
    }
  }

  @Override
  protected List<AnnotationErrorContext> doBiz(SearchAnnotationRequest request) {
    final Set<AnnotationTaskBlock> annotations =
        blockRepository.findByAnnotationTypeEqualsAndStateIn(
            AnnotationTypeEnum.getByValue(request.getAnnotationType()),
            Arrays.asList(AnnotationTaskState.PRE_CLEAN, AnnotationTaskState.FINISHED));
    log.info("search annotations, get back {} annotations", annotations.size());

    if (annotations.size() == 0) {
      return Collections.emptyList();
    }

    final Predicate<String> term =
        Pattern.compile(request.getTerm(), Pattern.CASE_INSENSITIVE).asPredicate();
    final Predicate<String> type =
        StringUtils.isBlank(request.getType())
            ? (s) -> true
            : Pattern.compile(request.getType(), Pattern.CASE_INSENSITIVE).asPredicate();

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
                                    entity.getTerm())))
            .collect(Collectors.toList());

    log.info("search annotations results {}", results.size());
    return results;
  }
}
