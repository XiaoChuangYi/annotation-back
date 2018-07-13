package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.RelationLimitRule;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.utils.entity.RelationEntity;
import cn.malgo.core.definition.Entity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CheckLegalRelationBeforeAddServiceImpl implements CheckLegalRelationBeforeAddService {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final RelationLimitRuleRepository relationLimitRuleRepository;

  public CheckLegalRelationBeforeAddServiceImpl(
      AnnotationCombineRepository annotationCombineRepository,
      RelationLimitRuleRepository relationLimitRuleRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.relationLimitRuleRepository = relationLimitRuleRepository;
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeAdd(
      AddAnnotationGroupRequest addAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock) {
    return isLegal(
        getEntityType(
            annotationTaskBlock.getAnnotation(), addAnnotationGroupRequest.getSourceTag()),
        getEntityType(
            annotationTaskBlock.getAnnotation(), addAnnotationGroupRequest.getTargetTag()),
        addAnnotationGroupRequest.getRelation());
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeUpdate(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock) {
    final RelationEntity relationEntity =
        AnnotationConvert.getRelationEntityFromAnnotation(
            annotationTaskBlock.getAnnotation(), updateAnnotationGroupRequest.getReTag());
    return isLegal(
        getEntityType(annotationTaskBlock.getAnnotation(), relationEntity.getSourceTag()),
        getEntityType(annotationTaskBlock.getAnnotation(), relationEntity.getTargetTag()),
        updateAnnotationGroupRequest.getRelation());
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeUpdateEntity(
      UpdateAnnotationRequest updateAnnotationRequest) {
    final List<RelationLimitRule> relationLimitRules = relationLimitRuleRepository.findAll();
    AnnotationCombine annotationCombine =
        annotationCombineRepository.getOne(updateAnnotationRequest.getId());
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
    final List<Entity> entities = AnnotationConvert.getEntitiesFromAnnotation(annotation);
    final List<RelationLimitRulePair> sourceRelationLimitRulePairs =
        getSourceRelationLimitRulePairs(
            annotation,
            updateAnnotationRequest.getTag(),
            updateAnnotationRequest.getNewType(),
            entities);
    final boolean isIllegal = isIllegal(sourceRelationLimitRulePairs, relationLimitRules);
    if (!isIllegal) {
      // 作为target继续
      final List<RelationLimitRulePair> targetRelationLimitRulePairs =
          getTargetRelationLimitRulePairs(
              annotation,
              updateAnnotationRequest.getTag(),
              updateAnnotationRequest.getNewType(),
              entities);
      return isIllegal(targetRelationLimitRulePairs, relationLimitRules);
    }
    return true;
  }

  private List<RelationLimitRulePair> getTargetRelationLimitRulePairs(
      String annotation, String tag, String newType, List<Entity> entities) {
    return AnnotationConvert.getRelationEntitiesFromAnnotation(annotation)
        .stream()
        .filter(relationEntity -> relationEntity.getTargetTag().equals(tag))
        .collect(Collectors.toList())
        .stream()
        .map(
            relationEntity ->
                new RelationLimitRulePair(
                    entities
                        .stream()
                        .filter(entity -> entity.getTag().equals(relationEntity.getSourceTag()))
                        .findFirst()
                        .get()
                        .getType()
                        .replace("-and", ""),
                    newType.replace("-and", ""),
                    relationEntity.getType()))
        .collect(Collectors.toList());
  }

  private List<RelationLimitRulePair> getSourceRelationLimitRulePairs(
      String annotation, String tag, String newType, List<Entity> entities) {
    return AnnotationConvert.getRelationEntitiesFromAnnotation(annotation)
        .stream()
        .filter(relationEntity -> relationEntity.getSourceTag().equals(tag))
        .collect(Collectors.toList())
        .stream()
        .map(
            relationEntity ->
                new RelationLimitRulePair(
                    newType.replace("-and", ""),
                    entities
                        .stream()
                        .filter(entity -> entity.getTag().equals(relationEntity.getTargetTag()))
                        .findFirst()
                        .get()
                        .getType()
                        .replace("-and", ""),
                    relationEntity.getType()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeUpdateEntity(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock) {
    final List<Entity> entities =
        AnnotationConvert.getEntitiesFromAnnotation(annotationTaskBlock.getAnnotation());
    final List<RelationLimitRule> relationLimitRules = relationLimitRuleRepository.findAll();

    final List<RelationLimitRulePair> sourceRelationLimitRulePairs =
        getSourceRelationLimitRulePairs(
            annotationTaskBlock.getAnnotation(),
            updateAnnotationGroupRequest.getTag(),
            updateAnnotationGroupRequest.getNewType(),
            entities);
    final boolean isIllegal = isIllegal(sourceRelationLimitRulePairs, relationLimitRules);
    if (!isIllegal) {
      // 作为target继续
      final List<RelationLimitRulePair> targetRelationLimitRulePairs =
          getTargetRelationLimitRulePairs(
              annotationTaskBlock.getAnnotation(),
              updateAnnotationGroupRequest.getTag(),
              updateAnnotationGroupRequest.getNewType(),
              entities);
      return isIllegal(targetRelationLimitRulePairs, relationLimitRules);
    }
    return true;
  }

  private boolean isIllegal(
      final List<RelationLimitRulePair> relationLimitRulePairs,
      final List<RelationLimitRule> relationLimitRules) {
    return relationLimitRulePairs
        .stream()
        .anyMatch(
            relationLimitRulePair ->
                relationLimitRules
                        .stream()
                        .filter(
                            relationLimitRule ->
                                relationLimitRule
                                        .getSource()
                                        .equals(relationLimitRulePair.getSourceType())
                                    && relationLimitRule
                                        .getTarget()
                                        .equals(relationLimitRulePair.getTargetType())
                                    && relationLimitRule
                                        .getRelationType()
                                        .equals(relationLimitRulePair.getRelationType()))
                        .count()
                    == 0);
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeUpdate(UpdateRelationRequest updateRelationRequest) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(updateRelationRequest.getId());
    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();

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
      final RelationEntity relationEntity =
          AnnotationConvert.getRelationEntityFromAnnotation(
              annotation, updateRelationRequest.getReTag());
      return isLegal(
          getEntityType(annotation, relationEntity.getSourceTag()),
          getEntityType(annotation, relationEntity.getTargetTag()),
          updateRelationRequest.getRelation());
    }
    return false;
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeAdd(AddRelationRequest addRelationRequest) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();

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
      return isLegal(
          getEntityType(annotation, addRelationRequest.getSourceTag()),
          getEntityType(annotation, addRelationRequest.getTargetTag()),
          addRelationRequest.getRelation());
    }
    return false;
  }

  private String getEntityType(String annotation, String entityTag) {
    final Entity finalEntity =
        AnnotationConvert.getEntitiesFromAnnotation(annotation)
            .stream()
            .filter(entity -> entity.getTag().equals(entityTag))
            .findFirst()
            .orElse(null);
    return finalEntity == null ? "" : finalEntity.getType();
  }

  private boolean isLegal(String sourceType, String targetType, String relation) {

    return relationLimitRuleRepository.isLegalRelation(
        sourceType.replace("-and", ""), targetType.replace("-and", ""), relation);
  }

  @lombok.Value
  static class RelationLimitRulePair {

    private final String sourceType;
    private final String targetType;
    private final String relationType;
  }
}
