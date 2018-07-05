package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.biz.base.TransactionalBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.AnnotationStateResetRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AnnotationReExaminationBiz
    extends TransactionalBiz<AnnotationStateResetRequest, Object> {
  private final AnnotationCombineRepository annotationCombineRepository;

  public AnnotationReExaminationBiz(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(AnnotationStateResetRequest annotationStateResetRequest)
      throws InvalidInputException {
    if (annotationStateResetRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (annotationStateResetRequest.getIdList() == null) {
      throw new InvalidInputException("invalid-idList", "参数idList为空");
    }
    if (annotationStateResetRequest.getIdList().size() <= 0) {
      throw new InvalidInputException("empty-id-list", "空id集合");
    }
  }

  @Override
  protected Object doBiz(
      int userId, int role, AnnotationStateResetRequest annotationStateResetRequest) {
    final List<AnnotationCombine> annotationCombineList =
        annotationCombineRepository.findAllByIdInAndIsTaskEquals(
            annotationStateResetRequest.getIdList());
    if (annotationCombineList.size() > 0) {
      if (role == AnnotationRoleStateEnum.admin.getRole()) { // 管理员发起重新审核
        final List<AnnotationCombine> annotationCombines =
            annotationCombineList
                .stream()
                .map(
                    x -> {
                      x.setState(AnnotationCombineStateEnum.preExamine.name());
                      if (x.getAnnotationType() == AnnotationTypeEnum.wordPos.ordinal()) {
                        x.setManualAnnotation(x.getReviewedAnnotation());
                      }
                      return x;
                    })
                .collect(Collectors.toList());
        annotationCombineRepository.saveAll(annotationCombines);
      } else {
        throw new BusinessRuleException("no-current-record", "没有当前数据");
      }
    }
    return null;
  }
}
