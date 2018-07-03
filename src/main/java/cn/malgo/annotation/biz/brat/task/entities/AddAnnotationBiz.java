package cn.malgo.annotation.biz.brat.task.entities;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.brat.AddAnnotationRequest;
import cn.malgo.annotation.service.AnnotationOperateService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/** Created by cjl on 2018/6/1. */
@Slf4j
@Component
public class AddAnnotationBiz
    extends BaseAnnotationBiz<AddAnnotationRequest, AnnotationCombineBratVO> {

  @Override
  protected void validateRequest(AddAnnotationRequest addAnnotationRequest)
      throws InvalidInputException {

    if (StringUtils.isBlank(addAnnotationRequest.getTerm())) {
      throw new InvalidInputException("invalid-term", "term参数为空");
    }
    if (StringUtils.isBlank(addAnnotationRequest.getType())) {
      throw new InvalidInputException("invalid-annotation-type", "type参数为空");
    }
    if (addAnnotationRequest.getStartPosition() < 0) {
      throw new InvalidInputException("invalid-start-position", "无效的startPosition");
    }
    if (addAnnotationRequest.getEndPosition() <= 0) {
      throw new InvalidInputException("invalid-end-position", "无效的endPosition");
    }
  }

  @Override
  protected void authorize(int userId, int role, AddAnnotationRequest addAnnotationRequest)
      throws BusinessRuleException {}

  @Override
  AnnotationCombineBratVO doInternalProcess(
      int role,
      AnnotationOperateService annotationOperateService,
      AnnotationCombine annotationCombine,
      AddAnnotationRequest addAnnotationRequest) {
    String annotation = annotationOperateService.addAnnotation(addAnnotationRequest, role);
    AnnotationCombineBratVO annotationCombineBratVO;
    if (role > 0 && role < AnnotationRoleStateEnum.labelStaff.getRole()) { // 管理员或者是审核人员级别
      annotationCombine.setReviewedAnnotation(annotation);
    }
    if (role >= AnnotationRoleStateEnum.labelStaff.getRole()) { // 标注人员
      annotationCombine.setFinalAnnotation(annotation);
    }
    annotationCombineBratVO = AnnotationConvert.convert2AnnotationCombineBratVO(annotationCombine);
    return annotationCombineBratVO;
  }
}
