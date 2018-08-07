package cn.malgo.annotation.biz.brat.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.AnnotationStateBatchRequest;
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
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.EXAMINE)
@Slf4j
public class AnnotationBatchExamineBiz
    extends TransactionalBiz<AnnotationStateBatchRequest, Object> {

  private final AnnotationRepository annotationRepository;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;
  private final AnnotationExamineService annotationExamineService;
  private final AnnotationBlockService annotationBlockService;
  private final CheckRelationEntityService checkRelationEntityService;
  private final AnnotationFactory annotationFactory;

  public AnnotationBatchExamineBiz(
      final AnnotationRepository annotationRepository,
      final ExtractAddAtomicTermService extractAddAtomicTermService,
      final AnnotationExamineService annotationExamineService,
      final AnnotationBlockService annotationBlockService,
      final CheckRelationEntityService checkRelationEntityService,
      final AnnotationFactory annotationFactory) {
    this.annotationRepository = annotationRepository;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
    this.annotationExamineService = annotationExamineService;
    this.annotationBlockService = annotationBlockService;
    this.checkRelationEntityService = checkRelationEntityService;
    this.annotationFactory = annotationFactory;
  }

  @Override
  protected void validateRequest(AnnotationStateBatchRequest request) throws InvalidInputException {
    if (request.getIds().size() == 0) {
      throw new InvalidInputException("invalid-id-list", "参数ids为空");
    }
  }

  @Override
  protected Object doBiz(AnnotationStateBatchRequest request, UserDetails user) {
    final List<AnnotationNew> annotationNews = annotationRepository.findAllById(request.getIds());
    if (annotationNews.size() > 0) {
      annotationNews
          .stream()
          .forEach(
              annotationNew -> {
                if (annotationNew.getAnnotationType() == AnnotationTypeEnum.relation
                    && checkRelationEntityService.hasIsolatedAnchor(
                        annotationFactory.create(annotationNew))) {
                  throw new BusinessRuleException(
                      "has-isolated-anchor-type",
                      String.format("标注id:%d含有孤立锚点，无法提交！", annotationNew.getId()));
                }
              });
      extractAddAtomicTermService.batchExtractAndAddAtomicTerm(annotationNews);
      final List<Long> forbidList = annotationExamineService.batchAnnotationExamine(annotationNews);
      // 批量保存到block表
      annotationBlockService.saveAnnotationAll(annotationRepository.saveAll(annotationNews));
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
