package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.biz.base.BaseBiz;
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
    final List<AnnotationCombine> annotationCombineList =
        annotationCombineRepository.findAllByIdInAndIsTaskEquals(
            annotationStateResetRequest.getIdList());
    if (annotationCombineList.size() > 0) {
      if (role == AnnotationRoleStateEnum.auditor.getRole()) { // 审核人员发起返工
        final List<AnnotationCombine> annotationCombines =
            annotationCombineList
                .stream()
                .map(
                    x -> {
                      x.setState(AnnotationCombineStateEnum.annotationProcessing.name());
                      if (x.getAnnotationType() == AnnotationTypeEnum.wordPos.ordinal()) {
                        // 分词类型用到了manual_annotation
                        x.setManualAnnotation(x.getFinalAnnotation());
                      }
                      return x;
                      // 分句和关联的内容返工后的数据还是在final_annotation
                    })
                .collect(Collectors.toList());
        annotationCombineRepository.saveAll(annotationCombines);
      }
    } else {
      throw new BusinessRuleException("no-current-record", "没有当前数据");
    }
    return null;
  }
}
