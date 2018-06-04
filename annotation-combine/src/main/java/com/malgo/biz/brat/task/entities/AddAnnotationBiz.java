package com.malgo.biz.brat.task.entities;

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
 * Created by cjl on 2018/6/1.
 */
@Component
@Slf4j
public class AddAnnotationBiz extends BaseBiz<AddAnnotationRequest, AnnotationCombineBratVO> {

  private final AnnotationOperateService finalAnnotationOperateService;
  private final AnnotationOperateService reviewAnnotationOperateService;
  private final AnnotationCombineRepository annotationCombineRepository;
  private int globalRole;


  public AddAnnotationBiz(
      @Qualifier("local-final") AnnotationOperateService finalAnnotationOperateService,
      @Qualifier("local-review") AnnotationOperateService reviewAnnotationOperateService,
      AnnotationCombineRepository annotationCombineRepository
  ) {
    this.finalAnnotationOperateService = finalAnnotationOperateService;
    this.reviewAnnotationOperateService = reviewAnnotationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;

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
    if (StringUtils.isBlank(addAnnotationRequest.getAnnotationType())) {
      throw new InvalidInputException("invalid-annotationType", "annotationType参数为空");
    }
    if (addAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
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
    globalRole = role;
    if (role > 2) {//标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<AnnotationCombine> optional = annotationCombineRepository
          .findById(addAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-current-record", "当前人员无权操作当前记录");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(AddAnnotationRequest addAnnotationRequest) {
    Optional<AnnotationCombine> optional = annotationCombineRepository
        .findById(addAnnotationRequest.getId());
    if (optional.isPresent()) {
      log.info("新增标注输入参数：{}", JSON.toJSONString(addAnnotationRequest));
      AnnotationCombine annotationCombine = optional.get();
      if (globalRole > 0 && globalRole < 3) {//管理员或者是审核人员级别
        String annotation = reviewAnnotationOperateService.addAnnotation(addAnnotationRequest);
        log.info("管理审核人员新增标注输出结果：{}", annotation);
        annotationCombine.setReviewedAnnotation(annotation);
        annotationCombine = annotationCombineRepository.save(annotationCombine);
        return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
      }
      if (globalRole >= 3) {//标注人员
        if (annotationCombine.getAnnotationType() == 1) {//当前标注类型为分句标注
          annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
          String annotation = finalAnnotationOperateService.addAnnotation(addAnnotationRequest);
          log.info("标注人员新增标注输出结果：{}", annotation);
          annotationCombine.setFinalAnnotation(annotation);
          annotationCombine = annotationCombineRepository.save(annotationCombine);
          return AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
        }
      }
    }
    return null;
  }
}
