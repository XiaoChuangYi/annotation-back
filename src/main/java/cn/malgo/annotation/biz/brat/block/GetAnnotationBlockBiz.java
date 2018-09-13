package cn.malgo.annotation.biz.brat.block;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dto.User;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.brat.GetAutoAnnotationRequest;
import cn.malgo.annotation.service.UserCenterService;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class GetAnnotationBlockBiz
    extends BaseBiz<GetAutoAnnotationRequest, AnnotationBlockBratVO> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final UserCenterService userCenterService;

  public GetAnnotationBlockBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final UserCenterService userCenterService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.userCenterService = userCenterService;
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
      final String state = annotationTaskBlock.getState().name();

      if (StringUtils.equalsAny(
          state,
          AnnotationTaskState.CREATED.name(),
          AnnotationTaskState.ANNOTATED.name(),
          AnnotationTaskState.PRE_CLEAN.name(),
          AnnotationTaskState.FINISHED.name())) {
        final Map<Long, String> longStringMap =
            userCenterService
                .getUsersByUserCenter()
                .parallelStream()
                .collect(Collectors.toMap(User::getUserId, User::getNickName));
        AnnotationBlockBratVO annotationBlockBratVO =
            AnnotationConvert.convert2AnnotationBlockBratVO(annotationTaskBlock);
        annotationBlockBratVO.setAssignee(
            longStringMap.getOrDefault(annotationBlockBratVO.getAssigneeId(), ""));
        return annotationBlockBratVO;
      } else {
        throw new InvalidInputException("no-permission-handle-current-record", "无权操作当前状态的block");
      }
    }
    return null;
  }
}
