package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.biz.base.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.AutoAnnotation;
import cn.malgo.annotation.dto.UpdateAnnotationAlgorithm;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.annotation.service.AlgorithmApiService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
public class AnnotationCommitBiz extends BaseBiz<CommitAnnotationRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;
  private final AlgorithmApiService algorithmApiService;

  @Autowired
  public AnnotationCommitBiz(
      AnnotationCombineRepository annotationCombineRepository,
      ExtractAddAtomicTermService extractAddAtomicTermService,
      AlgorithmApiService algorithmApiService) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
    this.algorithmApiService = algorithmApiService;
  }

  @Override
  protected void validateRequest(CommitAnnotationRequest commitAnnotationRequest)
      throws InvalidInputException {
    if (commitAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (commitAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected void authorize(int userId, int role, CommitAnnotationRequest commitAnnotationRequest)
      throws BusinessRuleException {
    if (role > 2) {
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(commitAnnotationRequest.getId());
      if (optional.isPresent()) {
        AnnotationCombine annotationCombine = optional.get();
        if (userId != annotationCombine.getAssignee()) {
          throw new BusinessRuleException("no-permission-commit-current-record", "当前用户没有权限提交该条记录");
        }
      }
    }
  }

  @Override
  protected Object doBiz(CommitAnnotationRequest commitAnnotationRequest) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(commitAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      annotationCombine.setState(AnnotationCombineStateEnum.preExamine.name());
      if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.wordPos.ordinal()) { // 分词标注提交
        UpdateAnnotationAlgorithm updateAnnotationAlgorithm =
            extractAddAtomicTermService.extractAndAddAtomicTerm(annotationCombine);
        updateAnnotationAlgorithm.setAutoAnnotation(commitAnnotationRequest.getAutoAnnotation());
        List<AutoAnnotation> autoAnnotationList =
            algorithmApiService.listRecombineAnnotationThroughAlgorithm(updateAnnotationAlgorithm);
        if (autoAnnotationList == null || autoAnnotationList.get(0) == null) {
          throw new BusinessRuleException("null-response", "调用算法后台数据返回null");
        }
        annotationCombine.setFinalAnnotation(autoAnnotationList.get(0).getAnnotation());
        annotationCombine.setManualAnnotation("");
      } else {
        // 分句，关联提交
        annotationCombine.setFinalAnnotation(annotationCombine.getManualAnnotation());
      }
      annotationCombine.setReviewedAnnotation(annotationCombine.getFinalAnnotation());
      annotationCombineRepository.save(annotationCombine);
    }
    return null;
  }
}
