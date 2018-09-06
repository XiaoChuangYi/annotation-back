package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.block.ResetAnnotationBlockRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequirePermission(Permissions.ADMIN)
@Slf4j
public class AnnotationBlockResetToAnnotationBiz
    extends TransactionalBiz<ResetAnnotationBlockRequest, List<Long>> {
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationBlockService blockService;

  public AnnotationBlockResetToAnnotationBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationBlockService blockService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.blockService = blockService;
  }

  @Override
  protected void validateRequest(ResetAnnotationBlockRequest request) throws InvalidInputException {
    if (request.getIds() == null || request.getIds().size() == 0) {
      throw new InvalidInputException("id-list-empty", "参数blockId集合为空");
    }

    if (!StringUtils.equalsAny(
        request.getAction(),
        AnnotationBlockActionEnum.RE_ANNOTATION.name(),
        AnnotationBlockActionEnum.RE_EXAMINE.name())) {
      throw new InvalidInputException("invalid-action", request.getAction() + "不是一个合法的action");
    }
  }

  @Override
  protected List<Long> doBiz(ResetAnnotationBlockRequest request) {
    final AnnotationBlockActionEnum action = AnnotationBlockActionEnum.valueOf(request.getAction());

    final List<AnnotationTaskBlock> blocks =
        annotationTaskBlockRepository.findAllByStateInAndIdIn(
            Arrays.asList(AnnotationTaskState.PRE_CLEAN, AnnotationTaskState.FINISHED),
            request.getIds());

    if (blocks.size() != request.getIds().size()) {
      log.warn("语料重新标注中存在非法ID: {}", request.getIds());
    }

    return blocks
        .stream()
        .map(block -> blockService.resetBlock(block, action, "reset").getId())
        .collect(Collectors.toList());
  }
}
