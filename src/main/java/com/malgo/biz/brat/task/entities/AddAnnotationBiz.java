package com.malgo.biz.brat.task.entities;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.service.AnnotationRelationService;
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
public class AddAnnotationBiz extends BaseBiz<AddAnnotationRequest, AnnotationCombineBratVO> {

  private final AnnotationOperateService finalAnnotationOperateService;
  private final AnnotationOperateService reviewAnnotationOperateService;
  private final AnnotationOperateService algorithmAnnotationOperateService;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AnnotationRelationService annotationRelationService;

  private int globalRole;

  public AddAnnotationBiz(
      @Qualifier("local-final") AnnotationOperateService finalAnnotationOperateService,
      @Qualifier("local-review") AnnotationOperateService reviewAnnotationOperateService,
      @Qualifier("algorithm") AnnotationOperateService algorithmAnnotationOperateService,
      AnnotationCombineRepository annotationCombineRepository,
      AnnotationRelationService annotationRelationService) {
    this.finalAnnotationOperateService = finalAnnotationOperateService;
    this.reviewAnnotationOperateService = reviewAnnotationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;
    this.annotationRelationService = annotationRelationService;
    this.algorithmAnnotationOperateService = algorithmAnnotationOperateService;
  }

  @Override
  protected void validateRequest(AddAnnotationRequest addAnnotationRequest)
      throws InvalidInputException {
    if (addAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (StringUtils.isBlank(addAnnotationRequest.getTerm())) {
      throw new InvalidInputException("invalid-term", "term参数为空");
    }
    if (StringUtils.isBlank(addAnnotationRequest.getType())) {
      throw new InvalidInputException("invalid-annotation-type", "type参数为空");
    }
    if (addAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (addAnnotationRequest.getStartPosition() < 0) {
      throw new InvalidInputException("invalid-start-position", "无效的startPosition");
    }
    if (addAnnotationRequest.getEndPosition() <= 0) {
      throw new InvalidInputException("invalid-end-position", "无效的endPosition");
    }
  }

  @Override
  protected void authorize(int userId, int role, AddAnnotationRequest addAnnotationRequest)
      throws BusinessRuleException {
    globalRole = role;
    if (role > 2) { // 标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(addAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "当前人员无权操作当前记录");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(
      int userId, int role, AddAnnotationRequest addAnnotationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      AnnotationCombineBratVO annotationCombineBratVO;
      if (role > 0 && role < 3) { // 管理员或者是审核人员级别
        String annotation = "";
        if (annotationCombine.getAnnotationType() == 0) { // 分词过算法
          annotation = algorithmAnnotationOperateService.addAnnotation(addAnnotationRequest, role);
        }
        if (annotationCombine.getAnnotationType() == 1) { // 分句
          annotation = finalAnnotationOperateService.addAnnotation(addAnnotationRequest, role);
        }
        if (annotationCombine.getAnnotationType() == 2) { // 关联
          annotation = annotationRelationService.addAnnotation(addAnnotationRequest, role);
        }
        annotationCombine.setReviewedAnnotation(annotation);
        annotationCombineBratVO =
            AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
        return annotationCombineBratVO;
      }
      if (role >= 3) { // 标注人员
        String annotation = "";
        annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
        annotationCombine = annotationCombineRepository.save(annotationCombine);
        if (annotationCombine.getAnnotationType() == 0) { // 分词
          annotation = algorithmAnnotationOperateService.addAnnotation(addAnnotationRequest, role);
        }
        if (annotationCombine.getAnnotationType() == 1) { // 分句
          annotation = finalAnnotationOperateService.addAnnotation(addAnnotationRequest, role);
        }
        if (annotationCombine.getAnnotationType() == 2) { // 关联
          annotation = annotationRelationService.addAnnotation(addAnnotationRequest, role);
        }
        annotationCombine.setFinalAnnotation(annotation);
        annotationCombineBratVO =
            AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
        return annotationCombineBratVO;
      } else {
        // "当前角色操作，标注类型不匹配");
        throw new BusinessRuleException("annotation-mismatching", "当前角色操作，标注类型不匹配");
      }
    }
    return null;
  }
}
