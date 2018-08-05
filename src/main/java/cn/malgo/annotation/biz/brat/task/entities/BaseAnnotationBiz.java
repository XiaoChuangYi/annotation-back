package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.brat.BaseAnnotationRequest;
import cn.malgo.annotation.service.AnnotationFactory;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.BusinessRuleException;
import cn.malgo.service.exception.InternalServerException;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import cn.malgo.service.model.UserDetails;
import java.util.Optional;
import javax.annotation.Resource;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class BaseAnnotationBiz<REQ extends BaseAnnotationRequest, AnnotationCombineBratVO>
    extends BaseBiz<REQ, AnnotationCombineBratVO> {

  @Qualifier("local")
  @Resource
  private AnnotationOperateService localAnnotationOperateService;

  @Qualifier("algorithm")
  @Resource
  private AnnotationOperateService algorithmAnnotationOperateService;

  @Qualifier("relation")
  @Resource
  private AnnotationOperateService annotationRelationService;

  @Resource private AnnotationCombineRepository annotationCombineRepository;

  @Resource private AnnotationFactory annotationFactory;

  public static void checkPermission(final AnnotationCombine annotation, final UserDetails user) {
    switch (annotation.getStateEnum()) {
      case unDistributed:
      case errorPass:
      case innerAnnotation:
      case examinePass:
        throw new BusinessRuleException("invalid-state", annotation.getState() + "不应该被继续标注");

      case preExamine:
      case abandon:
        if (!user.hasPermission(Permissions.EXAMINE)) {
          throw new BusinessRuleException("permission-denied", user.getId() + "无权限");
        }

        break;

      case preAnnotation:
      case annotationProcessing:
        if (!user.hasPermission(Permissions.ANNOTATE)) {
          throw new BusinessRuleException("permission-denied", user.getId() + "无权限");
        }

        if (user.getId() != annotation.getAssignee()) {
          throw new BusinessRuleException("permission-denied", user.getId() + "无权限");
        }

        break;

      default:
        throw new InternalServerException("未知状态");
    }
  }

  protected Annotation getAnnotation(final AnnotationCombine annotationCombine) {
    return this.annotationFactory.create(annotationCombine);
  }

  @Nullable
  private AnnotationOperateService getAnnotationOperateService(int annotationType) {
    switch (AnnotationTypeEnum.getByValue(annotationType)) {
      case wordPos:
        return algorithmAnnotationOperateService;

      case sentence:
        return localAnnotationOperateService;

      case relation:
        return annotationRelationService;
    }

    return null;
  }

  @Override
  protected void validateRequest(REQ baseAnnotationRequest) throws InvalidInputException {
    if (baseAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationCombineBratVO doBiz(final REQ req, final UserDetails user) {
    final Optional<AnnotationCombine> optional = annotationCombineRepository.findById(req.getId());

    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();
      checkPermission(annotationCombine, user);

      return this.doInternalProcess(
          getAnnotationOperateService(annotationCombine.getAnnotationType()),
          annotationCombine,
          req);
    }

    throw new NotFoundException("annotation-not-found", req.getId() + "不存在");
  }

  abstract AnnotationCombineBratVO doInternalProcess(
      AnnotationOperateService annotationOperateService,
      AnnotationCombine annotationCombine,
      REQ req);
}
