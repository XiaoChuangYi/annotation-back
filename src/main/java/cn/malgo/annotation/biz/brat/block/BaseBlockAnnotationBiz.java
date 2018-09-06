package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public abstract class BaseBlockAnnotationBiz<
        REQ extends BaseAnnotationRequest, AnnotationBlockBratVO>
    extends BaseBiz<REQ, AnnotationBlockBratVO> {

  @Resource private AnnotationTaskBlockRepository annotationTaskBlockRepository;

  @Resource private AnnotationFactory annotationFactory;

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
}
