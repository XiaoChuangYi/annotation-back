package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequirePermission(Permissions.ADMIN)
public class GetAnnotationBlockBiz
    extends BaseBiz<GetAutoAnnotationRequest, AnnotationBlockBratVO> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public GetAnnotationBlockBiz(AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(GetAutoAnnotationRequest baseAnnotationRequest)
      throws InvalidInputException {
    if (baseAnnotationRequest.getId() <= 0) {
      throw new InvalidInputException("invalid-id", "无效的id");
    }
  }

  @Override
  protected AnnotationBlockBratVO doBiz(GetAutoAnnotationRequest baseAnnotationRequest) {
    Optional<AnnotationTaskBlock> optional =
        annotationTaskBlockRepository.findById(baseAnnotationRequest.getId());
    if (optional.isPresent()) {
      final AnnotationTaskBlock annotationTaskBlock = optional.get();
      if (StringUtils.equalsAny(
          annotationTaskBlock.getState().name(),
          AnnotationTaskState.ANNOTATED.name(),
          AnnotationTaskState.FINISHED.name())) {
        return AnnotationConvert.convert2AnnotationBlockBratVO(annotationTaskBlock);
      } else {
        throw new InvalidInputException("no-permission-handle-current-record", "无权操作当前状态的block");
      }
    }
    return null;
  }
}
