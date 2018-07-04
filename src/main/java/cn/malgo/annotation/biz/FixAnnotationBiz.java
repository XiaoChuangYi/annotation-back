package cn.malgo.annotation.biz;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.TransactionalBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.AnnotationErrorContext;
import cn.malgo.annotation.dto.FixAnnotationEntity;
import cn.malgo.annotation.dto.FixAnnotationResult;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.BaseEntity;
import cn.malgo.annotation.enums.AnnotationFixLogStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.FixAnnotationErrorRequest;
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
  private final AnnotationCombineRepository annotationCombineRepository;
  private final FixAnnotationErrorService fixAnnotationErrorService;
  private final AnnotationFixLogService annotationFixLogService;

  @Autowired
  public FixAnnotationBiz(
      AnnotationFactory annotationFactory,
      AnnotationCombineRepository annotationCombineRepository,
      FixAnnotationErrorService fixAnnotationErrorService,
      AnnotationFixLogService annotationFixLogService) {
    this.annotationFactory = annotationFactory;
    this.annotationCombineRepository = annotationCombineRepository;
    this.fixAnnotationErrorService = fixAnnotationErrorService;
    this.annotationFixLogService = annotationFixLogService;
  }

  @Override
  protected void validateRequest(FixAnnotationErrorRequest request) throws InvalidInputException {}

  private FixAnnotationResult fixAnnotation(
      AnnotationCombine annotationCombine, int start, int end, List<FixAnnotationEntity> entities) {
    if (annotationCombine == null) {
      return new FixAnnotationResult(false, "ID不存在");
    }

    try {
      final boolean doFix = entities != null && entities.size() != 0;

      if (doFix) {
        final Annotation annotation = annotationFactory.create(annotationCombine);
        final List<Entity> fixedEntities =
            fixAnnotationErrorService.fixAnnotation(annotation, start, end, entities);
        fixedEntities.forEach(
            entity ->
                annotationFixLogService.insertOrUpdate(
                    annotation.getId(),
                    entity.getStart(),
                    entity.getEnd(),
                    AnnotationFixLogStateEnum.FIXED));
        annotationCombineRepository.save(annotationCombine);
      } else {
        annotationFixLogService.insertOrUpdate(
            annotationCombine.getId(), start, end, AnnotationFixLogStateEnum.SKIPPED);
      }

      return new FixAnnotationResult(true, null);
    } catch (IllegalArgumentException ex) {
      log.warn("标注状态错误: {}, state: {}", annotationCombine.getId(), annotationCombine.getState());
      return new FixAnnotationResult(false, "标注状态错误: " + annotationCombine.getState());
    } catch (Exception ex) {
      log.error("修复标注出错: " + annotationCombine.getId(), ex);
      return new FixAnnotationResult(false, "修复标注出错: " + ex.getMessage());
    }
  }

  @Override
  @Transactional
  protected List<FixAnnotationResult> doBiz(FixAnnotationErrorRequest request) {
    final Map<Integer, AnnotationCombine> idMap =
        annotationCombineRepository
            .findAllById(
                request
                    .getAnnotations()
                    .stream()
                    .map(AnnotationErrorContext::getId)
                    .collect(Collectors.toSet()))
            .stream()
            .collect(Collectors.toMap(BaseEntity::getId, annotation -> annotation));

    // 这儿不要并行，因为同一个标注可能存在多次被修改的可能性，并行会导致错误，除非我们以标注为单位并行并收集结果
    return request
        .getAnnotations()
        .stream()
        .map(
            annotation ->
                fixAnnotation(
                    idMap.get(annotation.getId()),
                    annotation.getStart(),
                    annotation.getEnd(),
                    request.getEntities()))
        .collect(Collectors.toList());
  }
}
