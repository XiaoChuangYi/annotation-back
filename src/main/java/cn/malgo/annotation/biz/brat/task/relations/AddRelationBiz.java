package cn.malgo.annotation.biz.brat.task.relations;

import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.AddRelationRequest;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.core.definition.Entity;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
@Slf4j
public class AddRelationBiz extends BaseRelationBiz<AddRelationRequest, AnnotationCombineBratVO> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final RelationLimitRuleRepository relationLimitRuleRepository;

  public AddRelationBiz(
      AnnotationCombineRepository annotationCombineRepository,
      RelationLimitRuleRepository relationLimitRuleRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.relationLimitRuleRepository = relationLimitRuleRepository;
  }

  @Override
  protected void validateRequest(AddRelationRequest addRelationRequest)
      throws InvalidInputException {
    if (StringUtils.isBlank(addRelationRequest.getSourceTag())) {
      throw new InvalidInputException("invalid-source-tag", "参数sourceTag为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getTargetTag())) {
      throw new InvalidInputException("invalid-target-tag", "参数targetTag为空");
    }
    if (StringUtils.isBlank(addRelationRequest.getRelation())) {
      throw new InvalidInputException("invalid-relation", "参数relation为空");
    }
  }

  @Override
  AnnotationCombineBratVO doInternalProcess(
      int role,
      RelationOperateService relationOperateService,
      AnnotationCombine annotationCombine,
      AddRelationRequest addRelationRequest) {
    final AnnotationCombineBratVO annotationCombineBratVO;
    if (checkRelationIsNotLegalBeforeAdd(addRelationRequest, role)) {
      throw new InvalidInputException("illegal-relation-can-not-add", "该关系被关联规则限制，无法新增");
    }
    final String annotation = relationOperateService.addRelation(addRelationRequest, role);
    if (role > 0 && role < AnnotationRoleStateEnum.labelStaff.getRole()) { // 管理员或者是审核人员级别
      annotationCombine.setReviewedAnnotation(annotation);
    }
    if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) { // 标注人员
      annotationCombine.setFinalAnnotation(annotation);
    }
    annotationCombineBratVO = AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
    return annotationCombineBratVO;
  }

  private boolean checkRelationIsNotLegalBeforeAdd(
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

      final List<String> entityTypes =
          entities
              .stream()
              .filter(
                  x ->
                      StringUtils.equalsAny(
                          x.getTag(),
                          addRelationRequest.getSourceTag(),
                          addRelationRequest.getTargetTag()))
              .map(entity -> entity.getType())
              .collect(Collectors.toList());

      if (entityTypes.size() == 2) {
        return relationLimitRuleRepository.isLegalRelation(
            entityTypes.get(0), entityTypes.get(1), addRelationRequest.getRelation());
      }
      // 自己关联自己的情况
      if (entityTypes.size() == 1) {
        return relationLimitRuleRepository.isLegalRelation(
            entityTypes.get(0), entityTypes.get(0), addRelationRequest.getRelation());
      }
    }
    return false;
  }
}
