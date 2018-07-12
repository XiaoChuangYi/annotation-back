package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.TransactionalBiz;
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
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.FixAnnotationErrorRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.AnnotationErrorFactory;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationFixLogService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequireRole(AnnotationRoleStateEnum.admin)
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
          "invalid-annotation-type", request.getErrorType() + "不是合法的错误类型");
    }

    final AnnotationErrorEnum errorType = AnnotationErrorEnum.values()[request.getErrorType()];
    if (request.getEntities() == null) {
      // reset
      if (!errorType.isCanReset()) {
        throw new InvalidInputException("invalid-reset-action", errorType + "不支持打回重审");
      }
    } else if (request.getEntities().size() != 0) {
      // 修复
      if (!errorType.isCanFix()) {
        throw new InvalidInputException("invalid-reset-action", errorType + "不支持批量修复");
      }
    }
  }

  private FixAnnotationResult fixAnnotation(
      final AnnotationErrorEnum errorType,
      final AnnotationTaskBlock block,
      final FixAnnotationErrorContext context,
      final FixAnnotationErrorData data) {
    if (block == null) {
      return new FixAnnotationResult(false, "ID不存在");
    }

    if (block.getAnnotationType() != errorType.getAnnotationType()) {
      return new FixAnnotationResult(false, "标注类型只能是: " + errorType.getAnnotationType());
    }

    // 0: reset
    // 1: fix
    // 2: skip
    int action;
    if (data.getEntities() == null) {
      action = 0;
    } else if (data.getEntities().size() == 0) {
      action = 2;
    } else {
      action = 1;
    }
    final Entity finalEntity =
        AnnotationConvert.getEntitiesFromAnnotation(block.getAnnotation())
            .stream()
            .filter(entity -> entity.getStart() == start && entity.getEnd() == end)
            .findFirst()
            .orElse(null);

    try {
      switch (action) {
        case 0:
          blockService.resetBlock(
              block,
              AnnotationBlockActionEnum.RE_EXAMINE,
              errorType + ":" + finalEntity == null ? "无对应的错误词term" : finalEntity.getTerm());
          break;

        case 1:
          final Annotation annotation = annotationFactory.create(block);
          final List<Entity> fixedEntities =
              annotationErrorFactory.getProvider(errorType).fix(annotation, context, data);
          fixedEntities.forEach(
              entity ->
                  annotationFixLogService.insertOrUpdate(
                      annotation.getId(),
                      entity.getStart(),
                      entity.getEnd(),
                      AnnotationFixLogStateEnum.FIXED));
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
      log.warn("标注状态错误: {}, state: {}, ex: {}", block.getId(), block.getState(), ex.getMessage());
      return new FixAnnotationResult(false, "标注状态错误: " + block.getState());
    } catch (Exception ex) {
      log.error("修复标注出错: " + block.getId(), ex);
      return new FixAnnotationResult(false, "修复标注出错: " + ex.getMessage());
    }
  }

  @Override
  @Transactional
  protected List<FixAnnotationResult> doBiz(final FixAnnotationErrorRequest request) {
    final Map<Integer, AnnotationTaskBlock> idMap =
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

    // 这儿不要并行，因为同一个标注可能存在多次被修改的可能性，并行会导致错误，除非我们以标注为单位并行并收集结果
    return request
        .getAnnotations()
        .stream()
        .map(
            annotation ->
                fixAnnotation(errorType, idMap.get(annotation.getId()), annotation, request))
        .collect(Collectors.toList());
  }
}
