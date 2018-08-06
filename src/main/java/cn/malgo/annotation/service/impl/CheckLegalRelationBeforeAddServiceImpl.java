package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.RelationLimitRule;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.RelationEntity;
import cn.malgo.core.definition.Entity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CheckLegalRelationBeforeAddServiceImpl implements CheckLegalRelationBeforeAddService {

  private final AnnotationRepository annotationRepository;
  private final RelationLimitRuleRepository relationLimitRuleRepository;

  public CheckLegalRelationBeforeAddServiceImpl(
      AnnotationRepository annotationRepository,
      RelationLimitRuleRepository relationLimitRuleRepository) {
    this.annotationRepository = annotationRepository;
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
    AnnotationNew annotationNew = annotationRepository.getOne(updateAnnotationRequest.getId());
    final List<Entity> entities =
        AnnotationConvert.getEntitiesFromAnnotation(getAnnotation(annotationNew));
    final List<RelationLimitRulePair> sourceRelationLimitRulePairs =
        getSourceRelationLimitRulePairs(
            getAnnotation(annotationNew),
            updateAnnotationRequest.getTag(),
            updateAnnotationRequest.getNewType(),
            entities);
    final boolean isIllegal = isIllegal(sourceRelationLimitRulePairs, relationLimitRules);
    if (!isIllegal) {
      // 作为target继续
      final List<RelationLimitRulePair> targetRelationLimitRulePairs =
          getTargetRelationLimitRulePairs(
              getAnnotation(annotationNew),
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
    final Optional<AnnotationNew> optional =
        annotationRepository.findById(updateRelationRequest.getId());
    if (optional.isPresent()) {
      final AnnotationNew annotationNew = optional.get();
      final RelationEntity relationEntity =
          AnnotationConvert.getRelationEntityFromAnnotation(
              getAnnotation(annotationNew), updateRelationRequest.getReTag());
      return isLegal(
          getEntityType(getAnnotation(annotationNew), relationEntity.getSourceTag()),
          getEntityType(getAnnotation(annotationNew), relationEntity.getTargetTag()),
          updateRelationRequest.getRelation());
    }
    return false;
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeAdd(AddRelationRequest addRelationRequest) {
    final Optional<AnnotationNew> optional =
        annotationRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      final AnnotationNew annotationNew = optional.get();
      return isLegal(
          getEntityType(getAnnotation(annotationNew), addRelationRequest.getSourceTag()),
          getEntityType(getAnnotation(annotationNew), addRelationRequest.getTargetTag()),
          addRelationRequest.getRelation());
    }
    return false;
  }

  private String getAnnotation(AnnotationNew annotationNew) {
    String annotation = "";
    if (annotationNew.getState() == AnnotationStateEnum.PRE_ANNOTATION
        || annotationNew.getState() == AnnotationStateEnum.ANNOTATION_PROCESSING) {
      annotation = annotationNew.getFinalAnnotation();
    }
    return annotation;
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
