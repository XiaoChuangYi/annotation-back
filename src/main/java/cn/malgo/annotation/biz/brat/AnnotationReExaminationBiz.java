package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.biz.BaseBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.exception.BusinessRuleException;
import cn.malgo.annotation.exception.InvalidInputException;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AnnotationReExaminationBiz extends BaseBiz<Integer, Object> {

  private final AnnotationCombineRepository annotationCombineRepository;

  public AnnotationReExaminationBiz(AnnotationCombineRepository annotationCombineRepository) {
    this.annotationCombineRepository = annotationCombineRepository;
  }

  @Override
  protected void validateRequest(Integer integer) throws InvalidInputException {
    if (integer.intValue() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected void authorize(int userId, int role, Integer integer) throws BusinessRuleException {}

  @Override
  protected Object doBiz(int userId, int role, Integer integer) {
    final Optional<AnnotationCombine> optional =
        annotationCombineRepository.findById(integer.intValue());
    if (optional.isPresent()) {
      final AnnotationCombine annotationCombine = optional.get();
      if (role == AnnotationRoleStateEnum.admin.getRole()) { // 管理员发起重新审核
        annotationCombine.setState(AnnotationCombineStateEnum.preAnnotation.name());
        annotationCombine.setManualAnnotation(annotationCombine.getReviewedAnnotation());
      }
      annotationCombineRepository.save(annotationCombine);
    } else {
      throw new BusinessRuleException("no-current-record", "没有当前数据");
    }
    return null;
  }
}
