package com.malgo.biz.brat.task.relations;

import com.alibaba.fastjson.JSON;
import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.UpdateRelationRequest;
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
public class UpdateRelationBiz extends BaseBiz<UpdateRelationRequest, AnnotationCombineBratVO> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final RelationOperateService finalRelationOperateService;
  private final RelationOperateService reviewRelationOperateService;
  private int globalRole;
  private int globalUserId;

  public UpdateRelationBiz(
      @Qualifier("final") RelationOperateService finalRelationOperateService,
      @Qualifier("review") RelationOperateService reviewRelationOperateService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.finalRelationOperateService = finalRelationOperateService;
    this.reviewRelationOperateService = reviewRelationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(UpdateRelationRequest updateRelationRequest)
      throws InvalidInputException {
    if (updateRelationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (updateRelationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(updateRelationRequest.getReTag())) {
      throw new InvalidInputException("invalid-reTag", "参数reTag为空");
    }
    if (StringUtils.isBlank(updateRelationRequest.getRelation())) {
      throw new InvalidInputException("invalid-relation", "参数relation为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, UpdateRelationRequest updateRelationRequest)
      throws BusinessRuleException {
    globalRole = role;
    globalUserId = userId;
    if (role > 2) { // 标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(updateRelationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "您无权操作该条记录");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(UpdateRelationRequest updateRelationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(updateRelationRequest.getId());
    if (optional.isPresent()) {
      //      log.info("更新关系输入参数：{}", JSON.toJSONString(updateRelationRequest));
      AnnotationCombine annotationCombine = optional.get();
      AnnotationCombineBratVO annotationCombineBratVO;
      if (globalRole > 0 && globalRole < 3) { // 管理员或者是审核人员级别
        if (annotationCombine.getAnnotationType() == 2) {
          String annotation = reviewRelationOperateService.updateRelation(updateRelationRequest);
          //          log.info("管理审核人员更新关系输出结果：{}", annotation);
          annotationCombine.setReviewedAnnotation(annotation);
          annotationCombine = annotationCombineRepository.save(annotationCombine);
          annotationCombineBratVO =
              AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
          //          OpLoggerUtil.info(globalUserId, globalRole, "update-relation", "success");
          return annotationCombineBratVO;
        }
      }
      if (globalRole >= 3) { // 标注人员
        if (annotationCombine.getAnnotationType() == 2) { // 当前标注类型为关联标注
          annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
          String annotation = finalRelationOperateService.updateRelation(updateRelationRequest);
          //          log.info("标注人员更新关系输出结果：{}", annotation);
          annotationCombine.setFinalAnnotation(annotation);
          annotationCombine = annotationCombineRepository.save(annotationCombine);
          annotationCombineBratVO =
              AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
          //          OpLoggerUtil.info(globalUserId, globalRole, "update-relation", "success");
          return annotationCombineBratVO;
        } else {
          //          OpLoggerUtil.info(globalUserId, globalRole, "update-relation",
          // "当前角色操作，标注类型不匹配");
          throw new BusinessRuleException("annotation-mismatching", "当前角色操作，标注类型不匹配");
        }
      }
    }
    //    OpLoggerUtil.info(globalUserId, globalRole, "delete-relation", "无对应id记录");
    return null;
  }
}
