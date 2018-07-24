package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CheckRelationEntityServiceImpl implements CheckRelationEntityService {

  @Override
  public boolean checkRelationEntityBeforeAdd(
      AddAnnotationRequest addAnnotationRequest, AnnotationCombine annotationCombine) {
    final List<Entity> entities = getEntities(annotationCombine);
    final List<EntityPair> entityPairs =
        entities
            .stream()
            .map(entity -> new EntityPair(entity.getStart(), entity.getEnd(), entity.getType()))
            .collect(Collectors.toList());
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
      UpdateAnnotationRequest updateAnnotationRequest, AnnotationCombine annotationCombine) {
    final List<Entity> entities = getEntities(annotationCombine);
    Optional<Entity> optional =
        entities
            .stream()
            .filter(entity -> entity.getTag().equals(updateAnnotationRequest.getTag()))
            .findFirst();
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
      return entities
          .stream()
          .noneMatch(
              entity ->
                  entity.getStart() == current.getStart()
                      && entity.getEnd() == current.getEnd()
                      && entity.getType() == updateAnnotationRequest.getNewType());
    }
    return false;
  }

  private List<Entity> getEntities(final AnnotationCombine annotationCombine) {
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
    return annotationDocument.getEntities();
  }

  @Value
  static class EntityPair {

    private final int start;
    private final int end;
    private final String type;
  }
}
