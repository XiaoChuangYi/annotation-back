package com.malgo.biz.brat.task.relations;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.enums.AnnotationRoleStateEnum;
import com.malgo.enums.AnnotationTypeEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.DeleteRelationRequest;
import com.malgo.service.RelationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.AnnotationCombineBratVO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
@Slf4j
public class DeleteRelationBiz extends BaseBiz<DeleteRelationRequest, AnnotationCombineBratVO> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final RelationOperateService relationOperateService;

  public DeleteRelationBiz(
      @Qualifier("task-relation") RelationOperateService relationOperateService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.relationOperateService = relationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(DeleteRelationRequest deleteRelationRequest)
      throws InvalidInputException {
    if (deleteRelationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (deleteRelationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(deleteRelationRequest.getReTag())) {
      throw new InvalidInputException("invalid-reTag", "参数reTag为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, DeleteRelationRequest deleteRelationRequest)
      throws BusinessRuleException {
    if (role > AnnotationRoleStateEnum.auditor.getRole()) { // 标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(deleteRelationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "当前人员无权操作该条记录!");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(
      int userId, int role, DeleteRelationRequest deleteRelationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(deleteRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      AnnotationCombineBratVO annotationCombineBratVO;
      if (role > 0 && role < AnnotationRoleStateEnum.labelStaff.getRole()) { // 管理员或者是审核人员级别
        if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.relation.getValue()) {
          String annotation = relationOperateService.deleteRelation(deleteRelationRequest);
          annotationCombine.setReviewedAnnotation(annotation);
          annotationCombineBratVO =
              AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
          return annotationCombineBratVO;
        }
      }
      if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) { // 标注人员
        if (annotationCombine.getAnnotationType()
            == AnnotationTypeEnum.relation.getValue()) { // 当前标注类型为关联标注
          annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
          String annotation = relationOperateService.deleteRelation(deleteRelationRequest);
          annotationCombine.setFinalAnnotation(annotation);
          annotationCombineBratVO =
              AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
          return annotationCombineBratVO;
        } else {
          // "当前角色操作，标注类型不匹配");
          throw new BusinessRuleException("annotation-mismatching", "当前角色操作，标注类型不匹配");
        }
      }
    }
    return null;
  }
}
