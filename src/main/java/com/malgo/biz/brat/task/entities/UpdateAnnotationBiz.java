package com.malgo.biz.brat.task.entities;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.UpdateAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
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
public class UpdateAnnotationBiz extends BaseBiz<UpdateAnnotationRequest, AnnotationCombineBratVO> {

  private final AnnotationOperateService finalAnnotationOperateService;
  private final AnnotationOperateService reviewAnnotationOperateService;
  private final AnnotationCombineRepository annotationCombineRepository;
  private int globalRole;

  public UpdateAnnotationBiz(
      @Qualifier("local-final") AnnotationOperateService finalAnnotationOperateService,
      @Qualifier("local-review") AnnotationOperateService reviewAnnotationOperateService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.finalAnnotationOperateService = finalAnnotationOperateService;
    this.reviewAnnotationOperateService = reviewAnnotationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(UpdateAnnotationRequest updateAnnotationRequest)
      throws InvalidInputException {
    if (updateAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (updateAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(updateAnnotationRequest.getTag())) {
      throw new InvalidInputException("invalid-tag", "参数tag不能为空");
    }
    if (StringUtils.isBlank(updateAnnotationRequest.getNewType())) {
      throw new InvalidInputException("invalid-new-type", "参数newType不能为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, UpdateAnnotationRequest updateAnnotationRequest)
      throws BusinessRuleException {
    globalRole = role;
    if (role > 2) { // 标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(updateAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "当前练习人员无权操作当前记录");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(
      int userId, int role, UpdateAnnotationRequest updateAnnotationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(updateAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      AnnotationCombineBratVO annotationCombineBratVO;
      if (role > 0 && role < 3) { // 管理员或者是审核人员级别
        String annotation = finalAnnotationOperateService.updateAnnotation(updateAnnotationRequest);
        annotationCombine.setReviewedAnnotation(annotation);
        //        annotationCombine = annotationCombineRepository.save(annotationCombine);
        annotationCombineBratVO =
            AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
        return annotationCombineBratVO;
      }
      if (role >= 3) { // 标注人员
        if (annotationCombine.getAnnotationType() != 0) {
          annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
          annotationCombine = annotationCombineRepository.save(annotationCombine);
          String annotation =
              finalAnnotationOperateService.updateAnnotation(updateAnnotationRequest);
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
