package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.ExtractAddAtomicTermService;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Optional;

public abstract class BaseBlockAnnotationBiz<
        REQ extends BaseAnnotationRequest, AnnotationBlockBratVO>
    extends BaseBiz<REQ, AnnotationBlockBratVO> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationFactory annotationFactory;
  private final ExtractAddAtomicTermService extractAddAtomicTermService;

  protected BaseBlockAnnotationBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationFactory annotationFactory,
      final ExtractAddAtomicTermService extractAddAtomicTermService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationFactory = annotationFactory;
    this.extractAddAtomicTermService = extractAddAtomicTermService;
  }

  @Override
  protected void validateRequest(REQ req) throws InvalidInputException {
    if (req.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationBlockBratVO doBiz(REQ req, UserDetails user) {
    final Optional<AnnotationTaskBlock> optional =
        annotationTaskBlockRepository.findById(req.getId());

    return optional
        .map(annotationTaskBlock -> this.doInternalProcess(annotationTaskBlock, req))
        .orElse(null);
  }

  abstract AnnotationBlockBratVO doInternalProcess(
      AnnotationTaskBlock annotationTaskBlock, REQ req);

  protected Annotation getAnnotation(final AnnotationTaskBlock annotationTaskBlock) {
    return this.annotationFactory.create(annotationTaskBlock);
  }

  protected AnnotationTaskBlock saveAnnotation(
      final AnnotationTaskBlock annotationTaskBlock, final String annotation) {
    annotationTaskBlock.setAnnotation(annotation);

    if (annotationTaskBlock.getState() == AnnotationTaskState.CREATED) {
      annotationTaskBlock.setState(AnnotationTaskState.PRE_CLEAN);
    }

    if (annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.wordPos
        || annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.disease) {
      extractAddAtomicTermService.extractAndAddAtomicTerm(getAnnotation(annotationTaskBlock));
    }

    return annotationTaskBlockRepository.save(annotationTaskBlock);
  }
}
