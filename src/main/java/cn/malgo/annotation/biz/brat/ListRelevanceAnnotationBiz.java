package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.block.ListRelevanceAnnotationRequest;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.utils.entity.RelationEntity;
import cn.malgo.core.definition.Entity;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class ListRelevanceAnnotationBiz
    extends BaseBiz<ListRelevanceAnnotationRequest, List<AnnotationTaskBlock>> {

  private final AnnotationTaskDocRepository annotationTaskDocRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public ListRelevanceAnnotationBiz(
      AnnotationTaskDocRepository annotationTaskDocRepository,
      AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskDocRepository = annotationTaskDocRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(ListRelevanceAnnotationRequest listRelevanceAnnotationRequest)
      throws InvalidInputException {
    if (listRelevanceAnnotationRequest.getTaskId() <= 0) {
      throw new InvalidInputException("invalid-task-id", "无效的taskId");
    }
  }

  @Override
  protected List<AnnotationTaskBlock> doBiz(
      int userId, int role, ListRelevanceAnnotationRequest listRelevanceAnnotationRequest) {
    // 获取blockId集合
    final List<Integer> blockIdList =
        annotationTaskDocRepository
            .findAllByTask(new AnnotationTask(listRelevanceAnnotationRequest.getTaskId()))
            .stream()
            .filter(
                annotationTaskDoc ->
                    annotationTaskDoc.getAnnotationType() == AnnotationTypeEnum.relation
                        && StringUtils.equalsAny(
                            annotationTaskDoc.getState().name(),
                            AnnotationTaskState.ANNOTATED.name(),
                            AnnotationTaskState.FINISHED.name()))
            .flatMap(x -> x.getBlocks().stream())
            .collect(Collectors.toList())
            .stream()
            .map(annotationTaskDocBlock -> annotationTaskDocBlock.getId().getBlockId())
            .collect(Collectors.toList());
    final List<RelationQueryPair> relationQueryPairs =
        annotationTaskBlockRepository
            .findAllById(blockIdList)
            .stream()
            .flatMap(
                annotationTaskBlock ->
                    Arrays.asList(
                            new BlockRelationPair(
                                annotationTaskBlock.getId(),
                                AnnotationConvert.getEntitiesFromAnnotation(
                                    annotationTaskBlock.getAnnotation()),
                                AnnotationConvert.getRelationEntitiesFromAnnotation(
                                    annotationTaskBlock.getAnnotation())))
                        .stream())
            .flatMap(
                pair ->
                    pair.getRelationEntities()
                        .stream()
                        .map(
                            relationEntity ->
                                new RelationQueryPair(
                                    pair.blockId,
                                    pair.getEntities()
                                        .stream()
                                        .filter(
                                            entity ->
                                                entity
                                                    .getTag()
                                                    .equals(relationEntity.getSourceTag()))
                                        .findFirst()
                                        .get()
                                        .getTerm(),
                                    pair.getEntities()
                                        .stream()
                                        .filter(
                                            entity ->
                                                entity
                                                    .getTag()
                                                    .equals(relationEntity.getSourceTag()))
                                        .findFirst()
                                        .get()
                                        .getType(),
                                    pair.getEntities()
                                        .stream()
                                        .filter(
                                            entity ->
                                                entity
                                                    .getTag()
                                                    .equals(relationEntity.getTargetTag()))
                                        .findFirst()
                                        .get()
                                        .getTerm(),
                                    pair.getEntities()
                                        .stream()
                                        .filter(
                                            entity ->
                                                entity
                                                    .getTag()
                                                    .equals(relationEntity.getTargetTag()))
                                        .findFirst()
                                        .get()
                                        .getType(),
                                    relationEntity.getType())))
            .collect(Collectors.toList());
    final List<RelationQueryPair> relationQueryPairList =
        relationQueryPairs
            .stream()
            .filter(
                relationQueryPair -> {
                  if (StringUtils.isNotBlank(listRelevanceAnnotationRequest.getRelation())) {
                    return relationQueryPair
                        .getRelation()
                        .equals(listRelevanceAnnotationRequest.getRelation());
                  } else {
                    return true;
                  }
                })
            .filter(
                relationQueryPair -> {
                  if (StringUtils.isNotBlank(listRelevanceAnnotationRequest.getSourceType())) {
                    return relationQueryPair
                        .getSourceType()
                        .equals(listRelevanceAnnotationRequest.getSourceType());
                  } else {
                    return true;
                  }
                })
            .filter(
                relationQueryPair -> {
                  if (StringUtils.isNotBlank(listRelevanceAnnotationRequest.getTargetType())) {
                    return relationQueryPair
                        .getTargetType()
                        .equals(listRelevanceAnnotationRequest.getTargetType());
                  } else {
                    return true;
                  }
                })
            .filter(
                relationQueryPair -> {
                  if (StringUtils.isNotBlank(listRelevanceAnnotationRequest.getSourceText())) {
                    return relationQueryPair
                        .getSourceTerm()
                        .contains(listRelevanceAnnotationRequest.getSourceText());
                  } else {
                    return true;
                  }
                })
            .filter(
                relationQueryPair -> {
                  if (StringUtils.isNotBlank(listRelevanceAnnotationRequest.getTargetText())) {
                    return relationQueryPair
                        .getTargetTerm()
                        .contains(listRelevanceAnnotationRequest.getTargetText());
                  } else {
                    return true;
                  }
                })
            .collect(Collectors.toList());
    return annotationTaskBlockRepository.findAllById(
        relationQueryPairList
            .stream()
            .map(relationQueryPair -> relationQueryPair.getBlockId())
            .collect(Collectors.toList()));
  }

  @lombok.Value
  static class RelationQueryPair {

    private final int blockId;
    private final String sourceTerm;
    private final String sourceType;
    private final String targetTerm;
    private final String targetType;
    private final String relation;
  }

  @lombok.Value
  static class BlockRelationPair {

    private final int blockId;
    private final List<Entity> entities;
    private final List<RelationEntity> relationEntities;
  }
}
