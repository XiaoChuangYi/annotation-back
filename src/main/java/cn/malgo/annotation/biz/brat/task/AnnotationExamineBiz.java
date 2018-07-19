package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequirePermission(Permissions.EXAMINE)
public class AnnotationExamineBiz extends TransactionalBiz<AnnotationStateRequest, Object> {
  private final AnnotationBlockService annotationBlockService;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;

  public AnnotationExamineBiz(
      final AnnotationBlockService annotationBlockService,
      final AnnotationCombineRepository annotationCombineRepository,
      final ExtractAddAtomicTermService extractAddAtomicTermService) {
    this.annotationBlockService = annotationBlockService;
    this.annotationCombineRepository = annotationCombineRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
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
  protected Object doBiz(AnnotationStateRequest annotationStateRequest, UserDetails user) {
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(annotationStateRequest.getId());

    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();

      if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.wordPos.ordinal()) {
        // 原子词入库
        extractAddAtomicTermService.extractAndAddAtomicTerm(annotationCombine);
      }

      if (annotationCombine.getState().equals(AnnotationCombineStateEnum.preExamine.name())) {
        final boolean equals =
            AnnotationConvert.compareAnnotation(
                annotationCombine.getFinalAnnotation(), annotationCombine.getReviewedAnnotation());

        if (equals) {
          annotationCombine.setState(AnnotationCombineStateEnum.examinePass.name());
        } else {
          annotationCombine.setState(AnnotationCombineStateEnum.errorPass.name());
        }
      } else if (annotationCombine.getState().equals(AnnotationCombineStateEnum.abandon.name())) {
        annotationCombine.setState(AnnotationCombineStateEnum.innerAnnotation.name());
      } else {
        throw new BusinessRuleException(
            "invalid-state", annotationCombine.getState() + "状态不可以被审核提交");
      }

      // 更新block
      annotationBlockService.saveAnnotation(annotationCombineRepository.save(annotationCombine));
    }

    return null;
  }
}
