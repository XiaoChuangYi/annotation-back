package com.malgo.biz.brat.task.algorithm;

import com.alibaba.fastjson.JSON;
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

/**
 * Created by cjl on 2018/6/1.
 */
@Component
@Slf4j
public class UpdateAnnotationAlgorithmBiz extends
    BaseBiz<UpdateAnnotationRequest, AnnotationCombineBratVO> {

  private final AnnotationOperateService algorithmAnnotationOperateService;
  private final AnnotationCombineRepository annotationCombineRepository;

  public UpdateAnnotationAlgorithmBiz(
      @Qualifier("algorithm") AnnotationOperateService algorithmAnnotationOperateService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.algorithmAnnotationOperateService = algorithmAnnotationOperateService;
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
      throw new InvalidInputException("invalid-newType", "参数newType不能为空");
    }
    if (StringUtils.isBlank(updateAnnotationRequest.getAutoAnnotation())) {
      throw new InvalidInputException("invalid-autoAnnotation", "autoAnnotation参数为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, UpdateAnnotationRequest updateAnnotationRequest)
      throws BusinessRuleException {
    if (role > 2) {
      Optional<AnnotationCombine> optional = annotationCombineRepository
          .findById(updateAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (userId == optional.get().getAssignee()) {
          throw new BusinessRuleException("no-authorize-current-record", "您无权操作当前记录! ");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(UpdateAnnotationRequest updateAnnotationRequest) {
    Optional<AnnotationCombine> optional = annotationCombineRepository
        .findById(updateAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      log.info("过算法后台，标注人员更新标注输入参数：{}", JSON.toJSONString(updateAnnotationRequest));
      annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      annotationCombine = annotationCombineRepository.save(annotationCombine);
      String annotation = algorithmAnnotationOperateService
          .updateAnnotation(updateAnnotationRequest);
      annotationCombine.setFinalAnnotation(annotation);
      log.info("过算法后台，标注人员更新标注输出结果：{}", annotation);
      return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
    }
    return null;
  }
}
