package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.biz.base.TransactionalBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.annotation.service.TaskDocService;
import cn.malgo.annotation.utils.AnnotationConvert;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AnnotationExamineBiz extends TransactionalBiz<AnnotationStateRequest, Object> {
  private final AnnotationBlockService annotationBlockService;
  private final TaskDocService taskDocService;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;

  public AnnotationExamineBiz(
      final AnnotationBlockService annotationBlockService,
      final TaskDocService taskDocService,
      final AnnotationCombineRepository annotationCombineRepository,
      final ExtractAddAtomicTermService extractAddAtomicTermService) {
    this.annotationBlockService = annotationBlockService;
    this.taskDocService = taskDocService;
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
            "invalid-annotation-state", annotationCombine.getState() + "状态不可以被审核提交");
      }

      // 更新block
      final AnnotationTaskBlock taskBlock =
          annotationBlockService.saveAnnotation(
              annotationCombineRepository.save(annotationCombine));

      // 更新所有对应的TaskDoc的状态
      taskBlock
          .getTaskDocs()
          .forEach(taskDocBlock -> taskDocService.updateState(taskDocBlock.getTaskDoc()));

      taskBlock
          .getTaskDocs()
          .stream()
          .map(taskDocBlock -> taskDocBlock.getTaskDoc().getTask())
          .collect(Collectors.toSet())
          .forEach(taskDocService::updateState);
    }

    return null;
  }
}
