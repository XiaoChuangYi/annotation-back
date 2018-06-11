package com.malgo.biz.brat.task.algorithm;

import com.alibaba.fastjson.JSON;
import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.DeleteAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
import com.malgo.utils.AnnotationConvert;
import com.malgo.utils.OpLoggerUtil;
import com.malgo.vo.AnnotationCombineBratVO;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/5/31. */
@Component
@Slf4j
public class DeleteAnnotationAlgorithmBiz
    extends BaseBiz<DeleteAnnotationRequest, AnnotationCombineBratVO> {

  private final AnnotationOperateService algorithmAnnotationOperateService;
  private final AnnotationCombineRepository annotationCombineRepository;
  private int globalRole;
  private int globalUserId;

  public DeleteAnnotationAlgorithmBiz(
      @Qualifier("algorithm") AnnotationOperateService algorithmAnnotationOperateService,
      AnnotationCombineRepository annotationCombineRepository) {
    this.algorithmAnnotationOperateService = algorithmAnnotationOperateService;
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(DeleteAnnotationRequest deleteAnnotationRequest)
      throws InvalidInputException {
    if (deleteAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (deleteAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
    if (StringUtils.isBlank(deleteAnnotationRequest.getTag())) {
      throw new InvalidInputException("invalid-id", "无效的tag参数");
    }
    if (StringUtils.isBlank(deleteAnnotationRequest.getAutoAnnotation())) {
      throw new InvalidInputException("invalid-auto-annotation", "autoAnnotation参数为空");
    }
  }

  @Override
  protected void authorize(int userId, int role, DeleteAnnotationRequest deleteAnnotationRequest)
      throws BusinessRuleException {
    globalUserId = userId;
    globalRole = role;
    if (role > 2) {
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(deleteAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (userId != optional.get().getAssignee()) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "您无权操作当前记录");
        }
      }
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(DeleteAnnotationRequest deleteAnnotationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(deleteAnnotationRequest.getId());
    if (optional.isPresent()) {
      //      log.info("过算法后台，标注人员删除标注输入参数：{}", JSON.toJSONString(deleteAnnotationRequest));
      AnnotationCombine annotationCombine = optional.get();
      annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      annotationCombine = annotationCombineRepository.save(annotationCombine);
      String annotation =
          algorithmAnnotationOperateService.deleteAnnotation(deleteAnnotationRequest);
      //      log.info("过算法后台，标注人员删除标注输出结果：{}", annotation);
      annotationCombine.setFinalAnnotation(annotation);
      AnnotationCombineBratVO annotationCombineBratVO =
          AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
      //      OpLoggerUtil.info(globalUserId, globalRole, "delete-annotation-algorithm", "success");
      return annotationCombineBratVO;
    }
    //    OpLoggerUtil.info(globalUserId, globalRole, "delete-annotation-algorithm", "无对应id记录");
    return null;
  }
}
