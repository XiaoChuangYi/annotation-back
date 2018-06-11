package com.malgo.biz.brat.task.relations;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.AddRelationRequest;
import com.malgo.service.RelationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.utils.OpLoggerUtil;
import com.malgo.vo.AnnotationCombineBratVO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
@Slf4j
public class AddRelationBiz extends BaseBiz<AddRelationRequest, AnnotationCombineBratVO> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final RelationOperateService finalRelationOperateService;
  private final RelationOperateService reviewRelationOperateService;
  private int globalRole;
  private int globalUserId;

  public AddRelationBiz(
      @Qualifier("final") RelationOperateService finalRelationOperateService,
      @Qualifier("review") RelationOperateService reviewRelationOperateService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.finalRelationOperateService = finalRelationOperateService;
    this.reviewRelationOperateService = reviewRelationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AddRelationRequest addRelationRequest)
      throws InvalidInputException {
    if (addRelationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (addRelationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
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
  protected void authorize(int userId, int role, AddRelationRequest addRelationRequest)
      throws BusinessRuleException {
    globalRole = role;
    globalUserId = userId;
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (role > 2) { // 标注人员，练习人员，需要判断是否有权限操作这一条
        if (annotationCombine.getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "当前人员无权操作该条记录");
        }
      }
      if (annotationCombine.getAnnotationType() != 2) {
        throw new BusinessRuleException("annotation-mismatching", "当前角色操作，标注类型不匹配");
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(AddRelationRequest addRelationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addRelationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      AnnotationCombineBratVO annotationCombineBratVO;
      if (globalRole > 0 && globalRole < 3) { // 管理员或者是审核人员级别
        if (annotationCombine.getAnnotationType() == 2) {
          String annotation = reviewRelationOperateService.addRelation(addRelationRequest);
          annotationCombine.setReviewedAnnotation(annotation);
          annotationCombine = annotationCombineRepository.save(annotationCombine);
          annotationCombineBratVO =
              AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
          OpLoggerUtil.info(globalUserId, globalRole, "add-relation", "success");
          return annotationCombineBratVO;
        }
      }
      if (globalRole >= 3) { // 标注人员
        if (annotationCombine.getAnnotationType() == 2) { // 当前标注类型为关联标注
          annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
          String annotation = finalRelationOperateService.addRelation(addRelationRequest);
          annotationCombine.setFinalAnnotation(annotation);
          annotationCombine = annotationCombineRepository.save(annotationCombine);
          annotationCombineBratVO =
              AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
          return annotationCombineBratVO;
        } else {
          throw new BusinessRuleException("annotation-mismatching", "当前角色操作，标注类型不匹配");
        }
      }
    }
    return null;
  }
}
