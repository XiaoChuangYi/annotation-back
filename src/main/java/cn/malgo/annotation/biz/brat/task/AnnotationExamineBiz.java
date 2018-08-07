package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.AnnotationStateRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.AnnotationExamineService;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.EXAMINE)
public class AnnotationExamineBiz extends TransactionalBiz<AnnotationStateRequest, Object> {

  private final AnnotationBlockService annotationBlockService;
  private final AnnotationRepository annotationRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;
  private final AnnotationExamineService annotationExamineService;
  private final CheckRelationEntityService checkRelationEntityService;
  private final AnnotationFactory annotationFactory;

  public AnnotationExamineBiz(
      final AnnotationBlockService annotationBlockService,
      final AnnotationRepository annotationRepository,
      final ExtractAddAtomicTermService extractAddAtomicTermService,
      final AnnotationExamineService annotationExamineService,
      final CheckRelationEntityService checkRelationEntityService,
      final AnnotationFactory annotationFactory) {
    this.annotationBlockService = annotationBlockService;
    this.annotationRepository = annotationRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
    this.annotationExamineService = annotationExamineService;
    this.checkRelationEntityService = checkRelationEntityService;
    this.annotationFactory = annotationFactory;
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
    Optional<AnnotationNew> optional =
        annotationRepository.findById(annotationStateRequest.getId());

    if (optional.isPresent()) {
      final AnnotationNew annotationNew = optional.get();
      if (annotationNew.getAnnotationType() == AnnotationTypeEnum.relation
          && checkRelationEntityService.hasIsolatedAnchor(
              annotationFactory.create(annotationNew))) {
        throw new BusinessRuleException("has-isolated-anchor-type", "含有孤立锚点，无法提交！");
      }
      if (annotationNew.getAnnotationType() == AnnotationTypeEnum.wordPos) {
        // 原子词入库
        extractAddAtomicTermService.extractAndAddAtomicTerm(annotationNew);
      }
      if (annotationExamineService.singleAnnotationExamine(annotationNew).intValue() > 0) {
        throw new BusinessRuleException("invalid-state", annotationNew.getState() + "状态不可以被审核提交");
      }
      // 更新block
      annotationBlockService.saveAnnotation(annotationRepository.save(annotationNew));
    }

    return null;
  }
}
