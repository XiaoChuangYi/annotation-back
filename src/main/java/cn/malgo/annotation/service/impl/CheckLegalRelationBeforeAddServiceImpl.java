package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.request.brat.UpdateAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.UpdateRelationRequest;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.utils.entity.RelationEntity;
import java.util.Optional;
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
      AnnotationTaskBlock annotationTaskBlock,
      int roleId) {
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
      AnnotationTaskBlock annotationTaskBlock,
      int roleId) {
    final RelationEntity relationEntity =
        AnnotationConvert.getRelationEntityFromAnnotation(
            annotationTaskBlock.getAnnotation(), updateAnnotationGroupRequest.getReTag());
    return isLegal(
        getEntityType(annotationTaskBlock.getAnnotation(), relationEntity.getSourceTag()),
        getEntityType(annotationTaskBlock.getAnnotation(), relationEntity.getTargetTag()),
        updateAnnotationGroupRequest.getRelation());
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeUpdate(
      UpdateRelationRequest updateRelationRequest, int roleId) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(updateRelationRequest.getId());
    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();
      final RelationEntity relationEntity =
          AnnotationConvert.getRelationEntityFromAnnotation(
              roleId >= AnnotationRoleStateEnum.labelStaff.getRole()
                  ? annotationCombine.getFinalAnnotation()
                  : annotationCombine.getReviewedAnnotation(),
              updateRelationRequest.getReTag());

      final String annotation =
          roleId >= AnnotationRoleStateEnum.labelStaff.getRole()
              ? annotationCombine.getFinalAnnotation()
              : annotationCombine.getReviewedAnnotation();

      return isLegal(
          getEntityType(annotation, relationEntity.getSourceTag()),
          getEntityType(annotation, relationEntity.getTargetTag()),
          updateRelationRequest.getRelation());
    }
    return false;
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeAdd(
      AddRelationRequest addRelationRequest, int roleId) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();
      final String annotation =
          roleId >= AnnotationRoleStateEnum.labelStaff.getRole()
              ? annotationCombine.getFinalAnnotation()
              : annotationCombine.getReviewedAnnotation();
      return isLegal(
          getEntityType(annotation, addRelationRequest.getSourceTag()),
          getEntityType(annotation, addRelationRequest.getTargetTag()),
          addRelationRequest.getRelation());
    }
    return false;
  }

  private String getEntityType(String annotation, String entityTag) {
    return AnnotationConvert.getEntitiesFromAnnotation(annotation)
        .stream()
        .filter(entity -> entity.getTag().equals(entityTag))
        .findFirst()
        .get()
        .getType();
  }

  private boolean isLegal(String sourceType, String targetType, String relation) {

    return relationLimitRuleRepository.isLegalRelation(
        sourceType.replace("-and", ""), targetType.replace("-and", ""), relation);
  }
}
