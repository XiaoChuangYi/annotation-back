package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Component
public class AnnotationCommitBiz extends BaseBiz<CommitAnnotationRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;

  @Autowired
  public AnnotationCommitBiz(
      AnnotationCombineRepository annotationCombineRepository,
      ExtractAddAtomicTermService extractAddAtomicTermService) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
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
      annotationCombine.setReviewedAnnotation(annotationCombine.getFinalAnnotation());
      if (annotationCombine.getAnnotationType()
          == AnnotationTypeEnum.wordPos.getValue()) { // 分词标注提交
        extractAddAtomicTermService.extractAndAddAtomicTerm(annotationCombine);
      }
      annotationCombineRepository.save(annotationCombine);
    }
    return null;
  }
}
