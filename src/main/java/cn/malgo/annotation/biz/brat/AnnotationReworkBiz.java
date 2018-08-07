package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationRepository;
import cn.malgo.annotation.entity.AnnotationNew;
import cn.malgo.annotation.enums.AnnotationStateEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.AnnotationStateResetRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.exception.NotFoundException;
import cn.malgo.service.model.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequirePermission(Permissions.ADMIN)
public class AnnotationReworkBiz extends BaseBiz<AnnotationStateResetRequest, Object> {
  private final AnnotationRepository annotationRepository;

  public AnnotationReworkBiz(AnnotationRepository annotationRepository) {
    this.annotationRepository = annotationRepository;
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
  protected Object doBiz(
      AnnotationStateResetRequest annotationStateResetRequest, UserDetails user) {
    final List<AnnotationNew> annotations =
        annotationRepository.findAllById(annotationStateResetRequest.getIdList());

    if (annotations.size() == 0) {
      throw new NotFoundException("annotation-not-found", "不存在");
    }

    annotations.forEach(
        x -> {
          x.setState(AnnotationStateEnum.ANNOTATION_PROCESSING);
          if (x.getAnnotationType() == AnnotationTypeEnum.wordPos) {
            // 分词类型用到了manual_annotation
            x.setManualAnnotation(x.getFinalAnnotation());
          }
          // 分句和关联的内容返工后的数据还是在final_annotation
        });

    annotationRepository.saveAll(annotations);
    return null;
  }
}
