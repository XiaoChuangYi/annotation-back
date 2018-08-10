package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.CommitAnnotationRequest;
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

  private final AnnotationCombineRepository annotationCombineRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;
  private final CheckRelationEntityService checkRelationEntityService;
  private final AnnotationFactory annotationFactory;

  @Autowired
  public AnnotationCommitBiz(
      final AnnotationCombineRepository annotationCombineRepository,
      final ExtractAddAtomicTermService extractAddAtomicTermService,
      final CheckRelationEntityService checkRelationEntityService,
      final AnnotationFactory annotationFactory) {
    this.annotationCombineRepository = annotationCombineRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
    this.checkRelationEntityService = checkRelationEntityService;
    this.annotationFactory = annotationFactory;
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
    Optional<AnnotationCombine> optional = annotationCombineRepository.findById(request.getId());

    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();
      if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.relation.ordinal()
          && checkRelationEntityService.hasIsolatedAnchor(
              annotationFactory.create(annotationCombine))) {
        throw new BusinessRuleException("has-isolated-anchor-type", "含有孤立锚点，无法提交！");
      }

      switch (annotationCombine.getStateEnum()) {
        case preAnnotation:
        case annotationProcessing:
          if (annotationCombine.getAssignee() != user.getId()) {
            throw new BusinessRuleException("permission-denied", "当前用户没有权限操作该条记录！");
          }

          break;

        default:
          throw new BusinessRuleException("invalid-state", "当前记录无法直接设定为'放弃'状态！");
      }

      // 目前直接通过，且不拷贝到review
      annotationCombine.setState(AnnotationCombineStateEnum.examinePass.name());
      //      annotationCombine.setState(AnnotationCombineStateEnum.preExamine.name());
      //      annotationCombine.setReviewedAnnotation(annotationCombine.getFinalAnnotation());
      annotationCombine.setCommitTimestamp(new Date());
      if (annotationCombine.getAnnotationType() == AnnotationTypeEnum.wordPos.ordinal()) { // 分词标注提交
        extractAddAtomicTermService.extractAndAddAtomicTerm(annotationCombine);
      }
      annotationCombineRepository.save(annotationCombine);
      return null;
    }

    throw new NotFoundException("annotation-not-found", request.getId() + "不存在");
  }
}
