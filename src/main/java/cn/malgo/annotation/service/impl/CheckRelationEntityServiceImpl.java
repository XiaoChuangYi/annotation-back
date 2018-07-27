package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;
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

  @Value
  static class EntityPair {
    private final int start;
    private final int end;
    private final String type;
  }
}
