package com.malgo.biz.brat.task.relations;

import com.malgo.biz.BaseBiz;
import com.malgo.dao.AnnotationCombineRepository;
import com.malgo.entity.AnnotationCombine;
import com.malgo.enums.AnnotationCombineStateEnum;
import com.malgo.enums.AnnotationRoleStateEnum;
import com.malgo.enums.AnnotationTypeEnum;
import com.malgo.exception.BusinessRuleException;
import com.malgo.exception.InvalidInputException;
import com.malgo.request.brat.BaseAnnotationRequest;
import com.malgo.service.RelationOperateService;
import java.util.Optional;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;

/** Created by cjl on 2018/6/14. */
public abstract class BaseRelationBiz<REQ extends BaseAnnotationRequest, AnnotationCombineBratVO>
    extends BaseBiz<REQ, AnnotationCombineBratVO> {

  @Resource private AnnotationCombineRepository annotationCombineRepository;

  @Qualifier("task-relation")
  @Resource
  private RelationOperateService relationOperateService;

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
    Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(baseAnnotationRequest.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      if (role > AnnotationRoleStateEnum.auditor.getRole()) { // 标注人员，练习人员，需要判断是否有权限操作这一条
        if (annotationCombine.getAssignee() != userId) {
          throw new BusinessRuleException("no-authorize-handle-current-record", "当前人员无权操作该条记录");
        }
      }
      if (annotationCombine.getAnnotationType() != AnnotationTypeEnum.relation.getValue()) {
        throw new BusinessRuleException("annotation-mismatching", "当前角色操作，标注类型不匹配");
      }
    }
  }

  @Override
  protected final AnnotationCombineBratVO doBiz(int userId, int role, REQ req) {
    Optional<AnnotationCombine> optional = annotationCombineRepository.findById(req.getId());
    if (optional.isPresent()) {
      AnnotationCombine annotationCombine = optional.get();
      annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
      return doInternalProcess(role, relationOperateService, annotationCombine, req);
    }
    return null;
  }

  abstract AnnotationCombineBratVO doInternalProcess(
      int role,
      RelationOperateService relationOperateService,
      AnnotationCombine annotationCombine,
      REQ req);
}