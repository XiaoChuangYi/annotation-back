package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.AnnotationStateResetRequest;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AnnotationReworkBiz extends BaseBiz<AnnotationStateResetRequest, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;

  public AnnotationReworkBiz(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AnnotationStateResetRequest annotationStateResetRequest)
      throws InvalidInputException {
    if (annotationStateResetRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (annotationStateResetRequest.getIdList().size() <= 0) {
      throw new InvalidInputException("empty-id-list", "空id集合");
    }
  }

  @Override
  protected void authorize(
      int userId, int role, AnnotationStateResetRequest annotationStateResetRequest)
      throws BusinessRuleException {
    if (role != AnnotationRoleStateEnum.auditor.getRole()) {
      throw new BusinessRuleException("no-permission-handle-current-record", "无权操作当前记录");
    }
  }

  @Override
  protected Object doBiz(
      int userId, int role, AnnotationStateResetRequest annotationStateResetRequest) {
    //    final Optional<AnnotationCombine> optional =
    // annotationCombineRepository.findAllByIdInAndIsTaskEquals();
    //    if (optional.isPresent()) {
    //      final AnnotationCombine annotationCombine = optional.get();
    //      if (role == AnnotationRoleStateEnum.auditor.getRole()) {//审核人员发起返工
    //        annotationCombine.setState(AnnotationCombineStateEnum.annotationProcessing.name());
    //        //返工如果是在原先的基础上做相应的操作
    //        annotationCombine.setManualAnnotation(annotationCombine.getFinalAnnotation());
    //      }
    //      annotationCombineRepository.save(annotationCombine);
    //    } else {
    //      throw new BusinessRuleException("no-current-record", "没有当前数据");
    //    }
    return null;
  }
}
