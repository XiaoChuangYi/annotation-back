package cn.malgo.annotation.service.impl;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.request.brat.AddAnnotationGroupRequest;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.service.CheckLegalRelationBeforeAddService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.core.definition.Entity;
import java.util.List;
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
    final List<Entity> entities =
        AnnotationConvert.getEntitiesFromAnnotation(annotationTaskBlock.getAnnotation());
    final String sourceType =
        entities
            .stream()
            .filter(entity -> entity.getTag().equals(addAnnotationGroupRequest.getSourceTag()))
            .findFirst()
            .get()
            .getType();
    final String targetType =
        entities
            .stream()
            .filter(entity -> entity.getTag().equals(addAnnotationGroupRequest.getTargetTag()))
            .findFirst()
            .get()
            .getType();
    return isLegal(sourceType, targetType, addAnnotationGroupRequest.getRelation());
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeAdd(
      AddRelationRequest addRelationRequest, int roleId) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();
      final List<Entity> entities =
          AnnotationConvert.getEntitiesFromAnnotation(
              roleId >= AnnotationRoleStateEnum.labelStaff.getRole()
                  ? annotationCombine.getFinalAnnotation()
                  : annotationCombine.getReviewedAnnotation());
      final String sourceType =
          entities
              .stream()
              .filter(entity -> entity.getTag().equals(addRelationRequest.getSourceTag()))
              .findFirst()
              .get()
              .getType();
      final String targetType =
          entities
              .stream()
              .filter(entity -> entity.getTag().equals(addRelationRequest.getTargetTag()))
              .findFirst()
              .get()
              .getType();

      return isLegal(sourceType, targetType, addRelationRequest.getRelation());
    }
    return false;
  }

  private boolean isLegal(String sourceType, String targetType, String relation) {

    return relationLimitRuleRepository.isLegalRelation(
        sourceType.replace("-and", ""), targetType.replace("-and", ""), relation);
  }
}
