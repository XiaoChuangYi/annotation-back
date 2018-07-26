package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.RelationEntity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckRelationEntityServiceImpl implements CheckRelationEntityService {

  private static final String SPECIAL_TYPE = "Anchor";

  @Override
  public boolean checkRelationEntityBeforeAdd(
      AddAnnotationRequest addAnnotationRequest, AnnotationCombine annotationCombine) {
    final List<Entity> entities = getAnnotationDocument(annotationCombine).getEntities();
    final List<EntityPair> entityPairs =
        entities
            .stream()
            .map(entity -> new EntityPair(entity.getStart(), entity.getEnd(), entity.getType()))
            .collect(Collectors.toList());
    if (SPECIAL_TYPE.equals(addAnnotationRequest.getType())) {
      return false;
    }
    return entities
        .stream()
        .anyMatch(
            entity -> {
              final boolean cross =
                  !(entity.getStart() >= addAnnotationRequest.getEndPosition()
                      || entity.getEnd() <= addAnnotationRequest.getStartPosition());
              if (cross) {
                if (entityPairs.contains(
                    new EntityPair(
                        addAnnotationRequest.getStartPosition(),
                        addAnnotationRequest.getEndPosition(),
                        addAnnotationRequest.getType()))) {
                  return false;
                } else {
                  return true;
                }
              } else {
                return false;
              }
            });
  }

  @Override
  public boolean checkRelationEntityBeforeUpdate(
      UpdateAnnotationRequest request, AnnotationCombine annotationCombine) {
    final List<Entity> entities = getAnnotationDocument(annotationCombine).getEntities();
    Optional<Entity> optional =
        entities.stream().filter(entity -> entity.getTag().equals(request.getTag())).findFirst();
    if (optional.isPresent()) {
      final Entity current = optional.get();
      if (entities
              .stream()
              .filter(
                  entity ->
                      entity.getStart() == current.getStart()
                          && entity.getEnd() == current.getEnd())
              .count()
          == 1) {
        return false;
      }
      // 更新为Anchor，不限制
      if (StringUtils.equals(SPECIAL_TYPE, request.getNewType())) {
        return false;
      }
      // 由Anchor更新为其它类型
      if (StringUtils.equals(current.getType(), SPECIAL_TYPE)
          && entities
                  .stream()
                  .filter(
                      entity ->
                          !entity.getType().equals(SPECIAL_TYPE)
                              && entity.getStart() == current.getStart()
                              && entity.getEnd() == current.getEnd())
                  .count()
              == 0) {
        return false;
      }

      if (StringUtils.equals(current.getType(), SPECIAL_TYPE)
          && entities
              .stream()
              .filter(
                  entity ->
                      !entity.getType().equals(SPECIAL_TYPE)
                          && entity.getStart() == current.getStart()
                          && entity.getEnd() == current.getEnd())
              .allMatch(entity -> entity.getType().equals(request.getNewType()))) {
        return false;
      }
      return entities
          .stream()
          .noneMatch(
              entity ->
                  entity.getStart() == current.getStart()
                      && entity.getEnd() == current.getEnd()
                      && entity.getType() == request.getNewType());
    }
    return false;
  }

  private AnnotationDocument getAnnotationDocument(final AnnotationCombine annotationCombine) {
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

  @Value
  static class EntityPair {

    private final int start;
    private final int end;
    private final String type;
  }

  @Override
  public boolean hasIsolatedAnchor(AnnotationCombine annotationCombine) {
    // todo 找出type为Anchor的标签，然后去relation中查找
    final List<Entity> entities = getAnnotationDocument(annotationCombine).getEntities();
    final List<RelationEntity> relationEntities =
        getAnnotationDocument(annotationCombine).getRelationEntities();
    final List<Entity> specialEntities =
        entities
            .stream()
            .filter(entity -> StringUtils.equals(entity.getType(), SPECIAL_TYPE))
            .collect(Collectors.toList());
    if (specialEntities.size() > 0) {
      return specialEntities
          .stream()
          .anyMatch(
              entity ->
                  relationEntities
                      .stream()
                      .noneMatch(
                          relationEntity ->
                              StringUtils.equalsAny(
                                  entity.getTag(),
                                  relationEntity.getSourceTag(),
                                  relationEntity.getTargetTag())));
    }
    return false;
  }
}
