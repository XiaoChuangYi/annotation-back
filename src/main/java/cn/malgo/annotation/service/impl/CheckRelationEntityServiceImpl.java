package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckRelationEntityServiceImpl implements CheckRelationEntityService {

  private static final String SPECIAL_TYPE = "Anchor";
  private static final String SPECIAL_RELATION_TYPE = "range";

  private boolean isValidEntity(
      final Entity entity, final String newType, final int start, final int end) {
    // 允许有Anchor或者相同类型的完全重叠的实体
    return SPECIAL_TYPE.equals(entity.getType())
        || (newType.equals(entity.getType())
            && start == entity.getStart()
            && end == entity.getEnd());
  }

  private boolean isMedian(int left, int middle, int right) {
    return (left < middle && middle < right) || (right < middle && middle < left);
  }

  @Override
  public boolean checkRelationEntityBeforeAdd(
      final AddAnnotationRequest request, final Annotation annotation) {
    if (SPECIAL_TYPE.equals(request.getType())) {
      return false;
    }

    return !annotation
        .getDocument()
        .getEntitiesIntersect(
            new BratPosition(request.getStartPosition(), request.getEndPosition()))
        .allMatch(
            entity ->
                isValidEntity(
                    entity,
                    request.getType(),
                    request.getStartPosition(),
                    request.getEndPosition()));
  }

  @Override
  public boolean checkRelationEntityBeforeUpdate(
      UpdateAnnotationRequest request, Annotation annotation) {
    if (StringUtils.equals(SPECIAL_TYPE, request.getNewType())) {
      // 不允许更新为Anchor
      return true;
    }

    final Entity current = annotation.getDocument().getEntity(request.getTag());
    if (current == null) {
      return false;
    }

    if (SPECIAL_TYPE.equals(current.getType())) {
      return true;
    }

    return !annotation
        .getDocument()
        .getEntitiesIntersect(new BratPosition(current.getStart(), current.getEnd()))
        .allMatch(
            entity ->
                entity == current
                    || isValidEntity(
                        entity,
                        request.getNewType(),
                        request.getStartPosition(),
                        request.getEndPosition()));
  }

  @Override
  public boolean hasIsolatedAnchor(Annotation annotation) {
    final List<Entity> specialEntities =
        annotation
            .getDocument()
            .getEntities()
            .stream()
            .filter(entity -> StringUtils.equals(entity.getType(), SPECIAL_TYPE))
            .collect(Collectors.toList());

    if (specialEntities.size() == 0) {
      return false;
    }

    return specialEntities
        .stream()
        .anyMatch(entity -> !annotation.getDocument().hasRelation(entity));
  }

  private List<Pair<Entity, Entity>> getRangeTypeEntityPairList(
      AnnotationCombine annotationCombine) {
    final List<Entity> sortedEntities =
        getAnnotationDocument(annotationCombine)
            .getEntities()
            .stream()
            .sorted(Comparator.comparing(entity -> entity.getStart()))
            .collect(Collectors.toList());
    return getAnnotationDocument(annotationCombine)
        .getRelationEntities()
        .stream()
        .filter(relationEntity -> relationEntity.getType().equals(SPECIAL_RELATION_TYPE))
        .map(
            relationEntity ->
                Pair.of(
                    sortedEntities
                        .stream()
                        .filter(
                            sourceEntity ->
                                sourceEntity.getTag().equals(relationEntity.getSourceTag()))
                        .findFirst()
                        .get(),
                    sortedEntities
                        .stream()
                        .filter(
                            targetEntity ->
                                targetEntity.getTag().equals(relationEntity.getTargetTag()))
                        .findFirst()
                        .get()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean addRelationEntityCheckAnchorSide(
      AddAnnotationRequest addAnnotationRequest, AnnotationCombine annotationCombine) {
    final List<String> sourceRangeTypes =
        getRangeTypeEntityPairList(annotationCombine)
            .stream()
            .filter(
                entityEntityPair ->
                    isMedian(
                        entityEntityPair.getLeft().getStart(),
                        addAnnotationRequest.getStartPosition(),
                        entityEntityPair.getRight().getStart()))
            .map(entityEntityPair -> entityEntityPair.getLeft().getType())
            .collect(Collectors.toList());
    if (sourceRangeTypes.contains(addAnnotationRequest.getType())) {
      return true;
    }
    return false;
  }

  private AnnotationDocument getAnnotationDocument(AnnotationCombine annotationCombine) {
    String annotation = "";
    if (StringUtils.equalsAny(
        annotationCombine.getState(),
        AnnotationCombineStateEnum.preAnnotation.name(),
        AnnotationCombineStateEnum.annotationProcessing.name())) {
      annotation = annotationCombine.getFinalAnnotation();
    }
    if (StringUtils.equalsAny(
        annotationCombine.getState(),
        AnnotationCombineStateEnum.abandon.name(),
        AnnotationCombineStateEnum.preExamine.name())) {
      annotation = annotationCombine.getReviewedAnnotation();
    }
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(annotation, annotationDocument);
    return annotationDocument;
  }

  @Override
  public boolean updateRelationEntityCheckAnchorSide(
      UpdateAnnotationRequest updateAnnotationRequest, AnnotationCombine annotationCombine) {
    // 通过tag找到具体的实体
    final Optional<Entity> optional =
        getAnnotationDocument(annotationCombine)
            .getEntities()
            .stream()
            .filter(entity -> StringUtils.equals(entity.getTag(), updateAnnotationRequest.getTag()))
            .findFirst();
    if (optional.isPresent()) {
      final Entity updateEntity = optional.get();
      final List<String> sourceRangeTypes =
          getRangeTypeEntityPairList(annotationCombine)
              .stream()
              .filter(
                  entityEntityPair ->
                      isMedian(
                          entityEntityPair.getLeft().getStart(),
                          updateEntity.getStart(),
                          entityEntityPair.getRight().getStart()))
              .map(entityEntityPair -> entityEntityPair.getLeft().getType())
              .collect(Collectors.toList());
      if (sourceRangeTypes.contains(updateAnnotationRequest.getNewType())) {
        return true;
      }
    }
    return false;
  }
}
