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
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CheckRelationEntityServiceImpl implements CheckRelationEntityService {

  private static final String SPECIAL_TYPE = "Anchor";

  private boolean isValidEntity(
      final Entity entity, final String newType, final int start, final int end) {
    // 允许有Anchor或者相同类型的完全重叠的实体
    return SPECIAL_TYPE.equals(entity.getType())
        || (newType.equals(entity.getType())
            && start == entity.getStart()
            && end == entity.getEnd());
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

  @Override
  public boolean addRelationEntityCheckAnchorSide(
      AddAnnotationRequest addAnnotationRequest, AnnotationCombine annotationCombine) {
    final List<Entity> sortedEntities =
        getAnnotationDocument(annotationCombine)
            .getEntities()
            .stream()
            .sorted(Comparator.comparing(entity -> entity.getStart()))
            .collect(Collectors.toList());
    final Entity[] leftEntity = new Entity[1];
    final Entity[] rightEntity = new Entity[1];
    // right增的情况，判断锚点前两个的实体是否重复
    IntStream.range(0, sortedEntities.size())
        .forEach(
            i -> {
              if ((i == 0 ? 0 : sortedEntities.get(i - 1).getStart())
                      < addAnnotationRequest.getStartPosition()
                  && addAnnotationRequest.getStartPosition() < sortedEntities.get(i).getStart()) {
                leftEntity[0] = i == 0 ? null : sortedEntities.get(i - 1);
                rightEntity[0] = sortedEntities.get(i);
              }
            });
    if (StringUtils.equals(rightEntity[0] == null ? "" : rightEntity[0].getType(), SPECIAL_TYPE)
        && StringUtils.equals(
            addAnnotationRequest.getType(), leftEntity[0] == null ? "" : leftEntity[0].getType())) {
      return true;
    }
    // left增的情况，判断锚点前两个的实体是否重复
    IntStream.range(0, sortedEntities.size())
        .forEach(
            k -> {
              if ((k == 0 ? 0 : sortedEntities.get(k - 1).getStart())
                      < addAnnotationRequest.getStartPosition()
                  && addAnnotationRequest.getStartPosition() < sortedEntities.get(k).getStart()) {
                leftEntity[0] = sortedEntities.get(k);
                rightEntity[0] =
                    (k + 1) >= sortedEntities.size() ? null : sortedEntities.get(k + 1);
              }
            });
    if (StringUtils.equals(rightEntity[0] == null ? "" : rightEntity[0].getType(), SPECIAL_TYPE)
        && StringUtils.equals(
            addAnnotationRequest.getType(), leftEntity[0] == null ? "" : leftEntity[0].getType())) {
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
    final List<Entity> sortedEntities =
        getAnnotationDocument(annotationCombine)
            .getEntities()
            .stream()
            .sorted(Comparator.comparing(entity -> entity.getStart()))
            .collect(Collectors.toList());
    final Optional<Entity> optional =
        sortedEntities
            .stream()
            .filter(entity -> StringUtils.equals(entity.getTag(), updateAnnotationRequest.getTag()))
            .findFirst();
    if (optional.isPresent()) {
      final Entity updateEntity = optional.get();
      final Entity[] leftEntity = new Entity[1];
      final Entity[] rightEntity = new Entity[1];
      IntStream.range(0, sortedEntities.size())
          .forEach(
              i -> {
                if (StringUtils.equals(
                    sortedEntities.get(i).getType(), updateEntity.getType())) { // 通过类型匹配到当前的entity
                  rightEntity[0] = sortedEntities.get(i + 1); // 获取右手边第一个entity
                  if (StringUtils.equals(
                      rightEntity[0].getType(), SPECIAL_TYPE)) { // 右手方向第一个匹配到特殊字符
                    leftEntity[0] = (i - 1) < 0 ? null : sortedEntities.get(i - 1); // 取得左手边的第一个
                  } else {
                    rightEntity[0] = sortedEntities.get(i + 2); // 获取右手边第二个entity
                    if (StringUtils.equals(rightEntity[0].getType(), SPECIAL_TYPE)) {
                      leftEntity[0] = sortedEntities.get(i + 1); // 获取右手边第一个
                    }
                  }
                }
              });
      if (StringUtils.equals(leftEntity[0].getType(), updateAnnotationRequest.getNewType())) {
        return true;
      }
    }
    return false;
  }
}
