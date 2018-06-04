package com.malgo.biz.brat.task.algorithm;

import com.alibaba.fastjson.JSON;
import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.AddAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.vo.AnnotationCombineBratVO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by cjl on 2018/5/31.
 */
@Component
@Slf4j
public class AddAnnotationAlgorithmBiz extends
    BaseBiz<AddAnnotationRequest, AnnotationCombineBratVO> {

  private final AnnotationOperateService annotationOperateService;
  private final AnnotationCombineRepository annotationCombineRepository;

  public AddAnnotationAlgorithmBiz(
      @Qualifier("algorithm") AnnotationOperateService annotationOperateService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.annotationOperateService = annotationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AddAnnotationRequest addAnnotationRequest)
      throws InvalidInputException {
    if (addAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (addAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(addAnnotationRequest.getTerm())) {
      throw new InvalidInputException("invalid-term", "term参数为空");
    }
    if (StringUtils.isBlank(addAnnotationRequest.getAnnotationType())) {
      throw new InvalidInputException("invalid-annotationType", "annotationType参数为空");
    }
    if (StringUtils.isBlank(addAnnotationRequest.getAutoAnnotation())) {
      throw new InvalidInputException("invalid-autoAnnotation", "autoAnnotation参数为空");
    }
    if (addAnnotationRequest.getStartPosition() < 0) {
      throw new InvalidInputException("invalid-startPosition", "无效的startPosition");
    }
    if (addAnnotationRequest.getEndPosition() <= 0) {
      throw new InvalidInputException("invalid-endPosition", "无效的endPosition");
    }

  }

  @Override
  protected void authorize(int userId, int role, AddAnnotationRequest addAnnotationRequest)
      throws BusinessRuleException {
    if (role > 2) {
      Optional<AnnotationCombine> optional = annotationCombineRepository
          .findById(addAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-current-record", "您无权操作当前记录!");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(AddAnnotationRequest addAnnotationRequest) {
    Optional<AnnotationCombine> optional = annotationCombineRepository
        .findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      log.info("过算法后台，标注人员新增标注输入参数：{}", JSON.toJSONString(addAnnotationRequest));
      AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getAnnotationType() == 0) {
        annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
        annotationCombine = annotationCombineRepository.save(annotationCombine);
        String annotation = annotationOperateService.addAnnotation(addAnnotationRequest);
        log.info("过算法后台，标注人员新增标注输出结果：{}", annotation);
        annotationCombine.setFinalAnnotation(annotation);
        return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
      }
    }
    return null;
  }
}
