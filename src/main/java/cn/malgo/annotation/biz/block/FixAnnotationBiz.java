package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.TransactionalBiz;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.AnnotationErrorContext;
import cn.malgo.annotation.dto.FixAnnotationEntity;
import cn.malgo.annotation.dto.FixAnnotationResult;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationFixLogStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.FixAnnotationErrorRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationFixLogService;
import cn.malgo.annotation.service.FixAnnotationErrorService;
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
  private final AnnotationTaskBlockRepository blockRepository;
  private final AnnotationBlockService blockService;
  private final FixAnnotationErrorService fixAnnotationErrorService;
  private final AnnotationFixLogService annotationFixLogService;

  @Autowired
  public FixAnnotationBiz(
      final AnnotationFactory annotationFactory,
      final AnnotationTaskBlockRepository blockRepository,
      final AnnotationBlockService blockService,
      final FixAnnotationErrorService fixAnnotationErrorService,
      final AnnotationFixLogService annotationFixLogService) {
    this.annotationFactory = annotationFactory;
    this.blockRepository = blockRepository;
    this.blockService = blockService;
    this.fixAnnotationErrorService = fixAnnotationErrorService;
    this.annotationFixLogService = annotationFixLogService;
  }

  @Override
  protected void validateRequest(FixAnnotationErrorRequest request) throws InvalidInputException {
    if (request.getErrorType() < 0
        || request.getErrorType() >= AnnotationErrorEnum.values().length) {
      throw new InvalidInputException(
          "invalid-annotation-type", request.getErrorType() + "不是合法的错误类型");
    }
  }

  private FixAnnotationResult fixAnnotation(
      final AnnotationErrorEnum errorType,
      final AnnotationTaskBlock block,
      final int start,
      final int end,
      final boolean doFix,
      final List<FixAnnotationEntity> entities) {
    if (block == null) {
      return new FixAnnotationResult(false, "ID不存在");
    }

    if (block.getAnnotationType() != errorType.getAnnotationType()) {
      return new FixAnnotationResult(false, "标注类型只能是: " + errorType.getAnnotationType());
    }

    try {
      if (doFix) {
        if (errorType == AnnotationErrorEnum.ISOLATED_ENTITY) {
          blockService.resetBlock(block, AnnotationBlockActionEnum.RE_EXAMINE);
        } else {
          final Annotation annotation = annotationFactory.create(block);
          final List<Entity> fixedEntities =
              fixAnnotationErrorService.fixAnnotationError(
                  errorType, annotation, start, end, entities);
          fixedEntities.forEach(
              entity ->
                  annotationFixLogService.insertOrUpdate(
                      annotation.getId(),
                      entity.getStart(),
                      entity.getEnd(),
                      AnnotationFixLogStateEnum.FIXED));
          blockRepository.save(block);
        }
      } else {
        annotationFixLogService.insertOrUpdate(
            block.getId(), start, end, AnnotationFixLogStateEnum.SKIPPED);
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
    final List<FixAnnotationEntity> entities = request.getEntities();
    final boolean doFix =
        errorType == AnnotationErrorEnum.ISOLATED_ENTITY
            ? entities != null
            : (entities != null && entities.size() != 0);

    // 这儿不要并行，因为同一个标注可能存在多次被修改的可能性，并行会导致错误，除非我们以标注为单位并行并收集结果
    return request
        .getAnnotations()
        .stream()
        .map(
            annotation ->
                fixAnnotation(
                    errorType,
                    idMap.get(annotation.getId()),
                    annotation.getStart(),
                    annotation.getEnd(),
                    doFix,
                    entities))
        .collect(Collectors.toList());
  }
}
