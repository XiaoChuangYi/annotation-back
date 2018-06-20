package com.malgo.biz.brat.task.entities;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.enums.AnnotationRoleStateEnum;
import com.malgo.enums.AnnotationTypeEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.BaseAnnotationRequest;
import com.malgo.service.AnnotationOperateService;
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
    if (baseAnnotationRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (baseAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected void authorize(int userId, int role, REQ baseAnnotationRequest)
      throws BusinessRuleException {
    if (role > AnnotationRoleStateEnum.auditor.getRole()) { // 标注人员，练习人员，需要判断是否有权限操作这一条
      Optional<AnnotationCombine> optional =
          annotationCombineRepository.findById(baseAnnotationRequest.getId());
      if (optional.isPresent()) {
        if (optional.get().getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "当前人员无权操作当前记录");
        }
      }
    }
  }

  @Override
  protected final AnnotationCombineBratVO doBiz(int userId, int role, REQ req) {
    final Optional<AnnotationCombine> optional = annotationCombineRepository.findById(req.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) {
        annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
        annotationCombine = annotationCombineRepository.save(annotationCombine);
      }
      return this.doInternalProcess(
          role,
          getAnnotationOperateService(annotationCombine.getAnnotationType()),
          annotationCombine,
          req);
    }
    return null;
  }

  abstract AnnotationCombineBratVO doInternalProcess(
      int role,
      AnnotationOperateService annotationOperateService,
      AnnotationCombine annotationCombine,
      REQ req);
}
