package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.AnnotationExamineService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.EXAMINE)
@Slf4j
public class AnnotationBatchExamineBiz extends TransactionalBiz<AnnotationStateRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;
  private final AnnotationExamineService annotationExamineService;
  private final AnnotationBlockService annotationBlockService;

  public AnnotationBatchExamineBiz(
      final AnnotationCombineRepository annotationCombineRepository,
      final ExtractAddAtomicTermService extractAddAtomicTermService,
      final AnnotationExamineService annotationExamineService,
      final AnnotationBlockService annotationBlockService) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
    this.annotationExamineService = annotationExamineService;
    this.annotationBlockService = annotationBlockService;
  }

  @Override
  protected void validateRequest(AnnotationStateRequest annotationStateRequest)
      throws InvalidInputException {
    if (annotationStateRequest.getIds().size() == 0) {
      throw new InvalidInputException("invalid-id-list", "参数ids为空");
    }
  }

  @Override
  protected Object doBiz(AnnotationStateRequest annotationStateRequest, UserDetails user) {
    final List<AnnotationCombine> annotationCombines =
        annotationCombineRepository.findAllById(annotationStateRequest.getIds());
    if (annotationCombines.size() > 0) {
      extractAddAtomicTermService.batchExtractAndAddAtomicTerm(annotationCombines);
      final List<Long> forbidList =
          annotationExamineService.batchAnnotationExamine(annotationCombines);
      // 批量保存到block表
      annotationBlockService.saveAnnotationAll(
          annotationCombineRepository.saveAll(annotationCombines));
      if (forbidList.size() > 0) {
        throw new BusinessRuleException(
            "some-annotation-invalid-state",
            String.format(
                "id为({%s})的标注的状态不可以被审核提交",
                forbidList
                    .stream()
                    .map(x -> String.valueOf(x.intValue()))
                    .collect(Collectors.joining(","))));
      }
    }
    return null;
  }
}
