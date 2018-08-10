package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.CheckRelationEntityService;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import cn.malgo.service.model.UserDetails;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ANNOTATE)
public class AnnotationCommitBiz extends BaseBiz<CommitAnnotationRequest, Object> {

  private final AnnotationRepository annotationRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;
  private final CheckRelationEntityService checkRelationEntityService;
  private final AnnotationFactory annotationFactory;
  private final AnnotationBlockService annotationBlockService;

  @Autowired
  public AnnotationCommitBiz(
      final AnnotationRepository annotationRepository,
      final ExtractAddAtomicTermService extractAddAtomicTermService,
      final CheckRelationEntityService checkRelationEntityService,
      final AnnotationFactory annotationFactory,
      final AnnotationBlockService annotationBlockService) {
    this.annotationRepository = annotationRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
    this.checkRelationEntityService = checkRelationEntityService;
    this.annotationFactory = annotationFactory;
    this.annotationBlockService = annotationBlockService;
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
  protected Object doBiz(CommitAnnotationRequest request, UserDetails user) {
    Optional<AnnotationNew> optional = annotationRepository.findById(request.getId());

    if (optional.isPresent()) {
      final AnnotationNew annotationNew = optional.get();
      if (annotationNew.getAnnotationType() == AnnotationTypeEnum.relation
          && checkRelationEntityService.hasIsolatedAnchor(
              annotationFactory.create(annotationNew))) {
        throw new BusinessRuleException("has-isolated-anchor-type", "含有孤立锚点，无法提交！");
      }

      switch (annotationNew.getState()) {
        case PRE_ANNOTATION:
        case ANNOTATION_PROCESSING:
          if (annotationNew.getAssignee() != user.getId()) {
            throw new BusinessRuleException("permission-denied", "当前用户没有权限操作该条记录！");
          }
          break;
        default:
          throw new BusinessRuleException("invalid-state", "当前记录无法直接设定为'放弃'状态！");
      }

      annotationNew.setState(AnnotationStateEnum.SUBMITTED);
      annotationNew.setCommitTimestamp(new Date());
      if (annotationNew.getAnnotationType() == AnnotationTypeEnum.wordPos) { // 分词标注提交
        extractAddAtomicTermService.extractAndAddAtomicTerm(annotationNew);
      }
      annotationBlockService.saveAnnotation(annotationRepository.save(annotationNew));
      return null;
    }
    throw new NotFoundException("annotation-not-found", request.getId() + "不存在");
  }
}
