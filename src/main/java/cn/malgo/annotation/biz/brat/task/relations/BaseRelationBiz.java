package cn.malgo.annotation.biz.brat.task.relations;

import cn.malgo.annotation.biz.brat.task.entities.BaseAnnotationBiz;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.service.RelationOperateService;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import cn.malgo.service.model.UserDetails;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.Optional;

public abstract class BaseRelationBiz<REQ extends BaseAnnotationRequest, AnnotationCombineBratVO>
    extends BaseBiz<REQ, AnnotationCombineBratVO> {

  @Resource private AnnotationRepository annotationRepository;

  @Qualifier("task-relation")
  @Resource
  private RelationOperateService relationOperateService;

  @Override
  protected void validateRequest(REQ baseAnnotationRequest) throws InvalidInputException {
    if (baseAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(final REQ req, final UserDetails user) {
    Optional<AnnotationNew> optional = annotationRepository.findById(req.getId());
    if (optional.isPresent()) {
      final AnnotationNew annotationNew = optional.get();
      BaseAnnotationBiz.checkPermission(annotationNew, user);
      return doInternalProcess(relationOperateService, annotationNew, req);
    }
    throw new NotFoundException("annotation-not-found", req.getId() + "未找到");
  }

  abstract AnnotationCombineBratVO doInternalProcess(
      RelationOperateService relationOperateService, AnnotationNew annotation, REQ req);
}
