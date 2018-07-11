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
      AnnotationTaskBlock annotationTaskBlock,
      int roleId) {
    final List<String> entityTypes =
        getEntityTypes(
            annotationTaskBlock.getAnnotation(),
            addAnnotationGroupRequest.getSourceTag(),
            addAnnotationGroupRequest.getTargetTag());
    return isLegal(entityTypes, addAnnotationGroupRequest.getRelation());
  }

  @Override
  public boolean checkRelationIsNotLegalBeforeUpdate(
      UpdateAnnotationGroupRequest updateAnnotationGroupRequest,
      AnnotationTaskBlock annotationTaskBlock,
      int roleId) {
    final RelationEntity relationEntity =
        AnnotationConvert.getRelationEntityFromAnnotation(
            annotationTaskBlock.getAnnotation(), updateAnnotationGroupRequest.getReTag());
    final List<String> entityTypes =
        getEntityTypes(
            annotationTaskBlock.getAnnotation(),
            relationEntity.getSourceTag(),
            relationEntity.getTargetTag());
    return isLegal(entityTypes, updateAnnotationGroupRequest.getRelation());
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
      final List<String> entityTypes =
          getEntityTypes(annotation, relationEntity.getSourceTag(), relationEntity.getTargetTag());
      return isLegal(entityTypes, updateRelationRequest.getRelation());
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
      final List<String> entityTypes =
          getEntityTypes(
              annotation, addRelationRequest.getSourceTag(), addRelationRequest.getTargetTag());
      return isLegal(entityTypes, addRelationRequest.getRelation());
    }
    return false;
  }

  private List<String> getEntityTypes(String annotation, String sourceTag, String targetTag) {
    final List<String> entityTypes =
        AnnotationConvert.getEntitiesFromAnnotation(annotation)
            .stream()
            .filter(x -> StringUtils.equalsAny(x.getTag(), sourceTag, targetTag))
            .map(entity -> entity.getType())
            .collect(Collectors.toList());
    return entityTypes;
  }

  private boolean isLegal(List<String> entityTypes, String relation) {
    if (entityTypes.size() == 2) {
      return relationLimitRuleRepository.isLegalRelation(
          entityTypes.get(0).replace("-and", ""), entityTypes.get(1).replace("-and", ""), relation);
    }
    // 自己关联自己的情况
    if (entityTypes.size() == 1) {
      return relationLimitRuleRepository.isLegalRelation(
          entityTypes.get(0).replace("-and", ""), entityTypes.get(0).replace("-and", ""), relation);
    }
    return false;
  }
}
