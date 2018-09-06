package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import java.util.Optional;
import javax.annotation.Resource;

public abstract class BaseAnnotationBiz<REQ extends BaseAnnotationRequest, AnnotationBratVO>
    extends BaseBiz<REQ, AnnotationBratVO> {
  @Resource private AnnotationRepository annotationRepository;

  @Resource private AnnotationFactory annotationFactory;

  protected Annotation getAnnotation(final AnnotationNew annotation) {
    return this.annotationFactory.create(annotation);
  }

  @Override
  protected void validateRequest(REQ baseAnnotationRequest) throws InvalidInputException {
    if (baseAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationBratVO doBiz(final REQ req) {
    final Optional<AnnotationNew> optional = annotationRepository.findById(req.getId());

    if (optional.isPresent()) {
      final AnnotationNew annotation = optional.get();
      return this.doInternalProcess(annotation, req);
    }

    throw new NotFoundException("annotation-not-found", req.getId() + "不存在");
  }

  abstract AnnotationBratVO doInternalProcess(AnnotationNew annotation, REQ req);
}
