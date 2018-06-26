package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.annotation.utils.AnnotationConvert;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/3. */
@Component
public class AnnotationExamineBiz extends BaseBiz<AnnotationStateRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;
  private final AlgorithmApiService algorithmApiService;

  public AnnotationExamineBiz(
      AnnotationCombineRepository annotationCombineRepository,
      ExtractAddAtomicTermService extractAddAtomicTermService,
      AlgorithmApiService algorithmApiService) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
    this.algorithmApiService = algorithmApiService;
  }

  @Override
  protected void validateRequest(AnnotationStateRequest annotationStateRequest)
      throws InvalidInputException {
    if (annotationStateRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (annotationStateRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected void authorize(int userId, int role, AnnotationStateRequest annotationStateRequest)
      throws BusinessRuleException {
    if (role > AnnotationRoleStateEnum.auditor.getRole()) {
      throw new BusinessRuleException("no-privilege-handle-current-record", "当前用户无权限进行该操作!");
    }
  }

  @Override
  protected Object doBiz(AnnotationStateRequest annotationStateRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(annotationStateRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      // 入库
      if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.wordPos.getValue()) { // 分词，入库
        UpdateAnnotationAlgorithm updateAnnotationAlgorithm =
            extractAddAtomicTermService.extractAndAddAtomicTerm(annotationCombine);
        updateAnnotationAlgorithm.setAutoAnnotation(annotationCombine.getFinalAnnotation());
        List<AutoAnnotation> autoAnnotationList =
            algorithmApiService.listRecombineAnnotationThroughAlgorithm(updateAnnotationAlgorithm);
        if (autoAnnotationList == null || autoAnnotationList.get(0) == null) {
          throw new BusinessRuleException("null-response", "调用算法后台数据返回null");
        }
        annotationCombine.setReviewedAnnotation(autoAnnotationList.get(0).getAnnotation());
      }
      if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.sentence.getValue()
          || annotationCombine.getAnnotationType() == AnnotationTypeEnum.relation.getValue()) {
        annotationCombine.setReviewedAnnotation(annotationCombine.getManualAnnotation());
      }
      // state handle
      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preExamine.name())) {
        boolean changed =
            AnnotationConvert.compareAnnotation(
                annotationCombine.getFinalAnnotation(), annotationCombine.getReviewedAnnotation());
        if (changed) { // 相同
          annotationCombine.setState(AnnotationCombineStateEnum.examinePass.name());
        } else { // 不同
          annotationCombine.setState(AnnotationCombineStateEnum.errorPass.name());
        }
      }
      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.abandon.name())) {
        annotationCombine.setState(AnnotationCombineStateEnum.innerAnnotation.name());
      }
      annotationCombineRepository.save(annotationCombine);
    }
    return null;
  }
}
