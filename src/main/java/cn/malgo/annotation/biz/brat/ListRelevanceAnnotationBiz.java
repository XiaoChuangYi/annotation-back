package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.controller.task.AnnotationTaskController;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.entity.AnnotationTask;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.block.ListRelevanceAnnotationRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.RelationEntity;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.core.definition.Entity;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
@Slf4j
public class ListRelevanceAnnotationBiz
    extends BaseBiz<ListRelevanceAnnotationRequest, PageVO<AnnotationBlockBratVO>> {

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
    if (listRelevanceAnnotationRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的pageIndex");
    }
    if (listRelevanceAnnotationRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的pageSize");
    }
  }

  @Override
  protected PageVO<AnnotationBlockBratVO> doBiz(
      int userId, int role, ListRelevanceAnnotationRequest listRelevanceAnnotationRequest) {
    log.info("开始获取blockId集合：{}", new Date());
    final List<Integer> blockIdList =
        getBlockIdListByTaskId(listRelevanceAnnotationRequest.getTaskId());
    log.info("开始获取RelationQueryPair集合：{}", new Date());
    final List<RelationQueryPair> relationQueryPairs = getRelationQueryPairs(blockIdList);
    log.info("开始获取条件过滤RelationQueryPair集合：{}", new Date());
    final List<RelationQueryPair> relationQueryPairList =
        filterRelationQueryPairsCondition(relationQueryPairs, listRelevanceAnnotationRequest);
    log.info("开始返回brat格式的RelationQueryPair集合：{}", new Date());
    final int skip =
        (listRelevanceAnnotationRequest.getPageIndex() - 1)
            * listRelevanceAnnotationRequest.getPageSize();
    final int limit = listRelevanceAnnotationRequest.getPageSize();
    final List<AnnotationTaskBlock> annotationTaskBlockList =
        annotationTaskBlockRepository.findAllById(
            relationQueryPairList
                .stream()
                .map(relationQueryPair -> relationQueryPair.getBlockId())
                .collect(Collectors.toList()));
    PageVO<AnnotationBlockBratVO> pageVO = new PageVO<>();
    pageVO.setTotal(annotationTaskBlockList.size());
    pageVO.setDataList(
        annotationTaskBlockList
            .stream()
            .skip(skip)
            .limit(limit)
            .map(
                annotationTaskBlock ->
                    AnnotationConvert.convert2AnnotationBlockBratVO(annotationTaskBlock))
            .collect(Collectors.toList()));
    return pageVO;
  }

  private List<Integer> getBlockIdListByTaskId(int taskId) {
    return annotationTaskDocRepository
        .findAllByTask(new AnnotationTask(taskId))
        .stream()
        .flatMap(x -> x.getBlocks().stream())
        .map(annotationTaskDocBlock -> annotationTaskDocBlock.getBlock())
        .filter(
            annotationTaskBlock ->
                annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.relation
                    && StringUtils.equalsAny(
                        annotationTaskBlock.getState().name(),
                        AnnotationTaskState.ANNOTATED.name(),
                        AnnotationTaskState.FINISHED.name()))
        .map(annotationTaskBlock -> annotationTaskBlock.getId())
        .collect(Collectors.toList());
  }

  private List<RelationQueryPair> getRelationQueryPairs(List<Integer> blockIdList) {
    return annotationTaskBlockRepository
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
                                            entity.getTag().equals(relationEntity.getSourceTag()))
                                    .findFirst()
                                    .get()
                                    .getTerm(),
                                pair.getEntities()
                                    .stream()
                                    .filter(
                                        entity ->
                                            entity.getTag().equals(relationEntity.getSourceTag()))
                                    .findFirst()
                                    .get()
                                    .getType(),
                                pair.getEntities()
                                    .stream()
                                    .filter(
                                        entity ->
                                            entity.getTag().equals(relationEntity.getTargetTag()))
                                    .findFirst()
                                    .get()
                                    .getTerm(),
                                pair.getEntities()
                                    .stream()
                                    .filter(
                                        entity ->
                                            entity.getTag().equals(relationEntity.getTargetTag()))
                                    .findFirst()
                                    .get()
                                    .getType(),
                                relationEntity.getType())))
        .collect(Collectors.toList());
  }

  private List<RelationQueryPair> filterRelationQueryPairsCondition(
      List<RelationQueryPair> relationQueryPairs,
      ListRelevanceAnnotationRequest listRelevanceAnnotationRequest) {
    return relationQueryPairs
        .stream()
        .filter(
            relationQueryPair -> {
              if (StringUtils.isNotBlank(listRelevanceAnnotationRequest.getRelation())) {
                return relationQueryPair
                    .getRelation()
                    .toLowerCase()
                    .equals(listRelevanceAnnotationRequest.getRelation().toLowerCase());
              } else {
                return true;
              }
            })
        .filter(
            relationQueryPair -> {
              if (StringUtils.isNotBlank(listRelevanceAnnotationRequest.getSourceType())) {
                return relationQueryPair
                    .getSourceType()
                    .toLowerCase()
                    .equals(listRelevanceAnnotationRequest.getSourceType().toLowerCase());
              } else {
                return true;
              }
            })
        .filter(
            relationQueryPair -> {
              if (StringUtils.isNotBlank(listRelevanceAnnotationRequest.getTargetType())) {
                return relationQueryPair
                    .getTargetType()
                    .toLowerCase()
                    .equals(listRelevanceAnnotationRequest.getTargetType().toLowerCase());
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
