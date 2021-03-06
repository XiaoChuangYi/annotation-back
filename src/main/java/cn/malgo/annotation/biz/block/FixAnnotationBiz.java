package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AnnotationErrorContext;
import cn.malgo.annotation.dto.error.FixAnnotationErrorContext;
import cn.malgo.annotation.dto.error.FixAnnotationErrorData;
import cn.malgo.annotation.dto.error.FixAnnotationResult;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationFixLogStateEnum;
import cn.malgo.annotation.request.FixAnnotationErrorRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.AnnotationErrorFactory;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationFixLogService;
import cn.malgo.core.definition.Entity;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InternalServerException;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FixAnnotationBiz
    extends TransactionalBiz<FixAnnotationErrorRequest, List<FixAnnotationResult>> {

  private final AnnotationFactory annotationFactory;
  private final AnnotationErrorFactory annotationErrorFactory;
  private final AnnotationTaskBlockRepository blockRepository;
  private final AnnotationBlockService blockService;
  private final AnnotationFixLogService annotationFixLogService;

  @Autowired
  public FixAnnotationBiz(
      final AnnotationFactory annotationFactory,
      final AnnotationErrorFactory annotationErrorFactory,
      final AnnotationTaskBlockRepository blockRepository,
      final AnnotationBlockService blockService,
      final AnnotationFixLogService annotationFixLogService) {
    this.annotationFactory = annotationFactory;
    this.annotationErrorFactory = annotationErrorFactory;
    this.blockRepository = blockRepository;
    this.blockService = blockService;
    this.annotationFixLogService = annotationFixLogService;
  }

  @Override
  protected void validateRequest(FixAnnotationErrorRequest request) throws InvalidInputException {
    if (request.getErrorType() < 0
        || request.getErrorType() >= AnnotationErrorEnum.values().length) {
      throw new InvalidInputException(
          "invalid-annotation-type", request.getErrorType() + "???????????????????????????");
    }

    final AnnotationErrorEnum errorType = AnnotationErrorEnum.values()[request.getErrorType()];
    if (request.getEntities() == null) {
      // reset
      if (!errorType.isCanReset()) {
        throw new InvalidInputException("invalid-reset-action", errorType + "?????????????????????");
      }
    } else if (request.getEntities().size() != 0) {
      // ??????
      if (!errorType.isCanFix()) {
        throw new InvalidInputException("invalid-reset-action", errorType + "?????????????????????");
      }
    }
  }

  private FixAnnotationResult fixAnnotation(
      final AnnotationErrorEnum errorType,
      final AnnotationTaskBlock block,
      final boolean saveToFixLog,
      final FixAnnotationErrorContext context,
      final FixAnnotationErrorData data) {
    if (block == null) {
      return new FixAnnotationResult(false, "ID?????????");
    }

    if (block.getAnnotationType() != errorType.getAnnotationType()) {
      return new FixAnnotationResult(false, "?????????????????????: " + errorType.getAnnotationType());
    }

    // 0: reset
    // 1: fix
    // 2: skip
    int action;
    if (data.getEntities() == null) {
      throw new InternalServerException("entities????????????null");
    } else if (data.getEntities().size() == 0) {
      action = 2;
    } else {
      action = 1;
    }

    try {
      switch (action) {
        case 1:
          final Annotation annotation = annotationFactory.create(block);
          final List<Entity> fixedEntities =
              annotationErrorFactory.getProvider(errorType).fix(annotation, context, data);

          if (saveToFixLog) {
            fixedEntities.forEach(
                entity ->
                    annotationFixLogService.insertOrUpdate(
                        annotation.getId(),
                        entity.getStart(),
                        entity.getEnd(),
                        AnnotationFixLogStateEnum.FIXED));
          }

          blockRepository.save(block);
          break;

        case 2:
          annotationFixLogService.insertOrUpdate(
              block.getId(),
              context.getStart(),
              context.getEnd(),
              AnnotationFixLogStateEnum.SKIPPED);
          break;
      }

      return new FixAnnotationResult(true, null);
    } catch (IllegalArgumentException ex) {
      log.warn("??????????????????: {}, state: {}, ex: {}", block.getId(), block.getState(), ex.getMessage());
      return new FixAnnotationResult(false, "??????????????????: " + block.getState());
    } catch (Exception ex) {
      log.error("??????????????????: " + block.getId(), ex);
      return new FixAnnotationResult(false, "??????????????????: " + ex.getMessage());
    }
  }

  private Map<Long, FixAnnotationResult> resetBlocks(
      final Map<Long, AnnotationTaskBlock> idMap,
      final AnnotationErrorEnum errorType,
      final String word) {
    final String comment = errorType.name() + ": " + word;
    return idMap
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                  final AnnotationTaskBlock block = entry.getValue();
                  try {
                    blockService.resetBlock(block, AnnotationBlockActionEnum.RE_EXAMINE, comment);
                    return new FixAnnotationResult(true, null);
                  } catch (IllegalArgumentException ex) {
                    log.warn(
                        "??????????????????: {}, state: {}, ex: {}",
                        block.getId(),
                        block.getState(),
                        ex.getMessage());
                    return new FixAnnotationResult(false, "??????????????????: " + block.getState());
                  }
                }));
  }

  @Override
  protected List<FixAnnotationResult> doBiz(final FixAnnotationErrorRequest request) {
    final Map<Long, AnnotationTaskBlock> idMap =
        blockRepository
            .findAllById(
                request
                    .getAnnotations()
                    .stream()
                    .map(AnnotationErrorContext::getId)
                    .collect(Collectors.toSet()))
            .stream()
            .collect(Collectors.toMap(AnnotationTaskBlock::getId, annotation -> annotation));

    final AnnotationErrorEnum errorType = AnnotationErrorEnum.values()[request.getErrorType()];
    final String word = request.getWord();

    if (request.getEntities() == null) {
      // ????????????
      final Map<Long, FixAnnotationResult> resultMap = resetBlocks(idMap, errorType, word);
      return request
          .getAnnotations()
          .stream()
          .map(annotation -> resultMap.get(annotation.getId()))
          .collect(Collectors.toList());
    }

    // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    return request
        .getAnnotations()
        .stream()
        .map(
            annotation ->
                fixAnnotation(
                    errorType,
                    idMap.get(annotation.getId()),
                    request.isSaveToFixLog(),
                    annotation,
                    request))
        .collect(Collectors.toList());
  }
}
