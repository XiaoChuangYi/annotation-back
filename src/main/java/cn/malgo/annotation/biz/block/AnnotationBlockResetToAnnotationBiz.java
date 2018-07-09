package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.TransactionalBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.block.ResetAnnotationBlockRequest;
import cn.malgo.annotation.service.AnnotationBlockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
@Slf4j
public class AnnotationBlockResetToAnnotationBiz
    extends TransactionalBiz<ResetAnnotationBlockRequest, List<Integer>> {
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AnnotationBlockService blockService;

  public AnnotationBlockResetToAnnotationBiz(
      final AnnotationTaskBlockRepository annotationTaskBlockRepository,
      final AnnotationCombineRepository annotationCombineRepository,
      final AnnotationBlockService blockService) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationCombineRepository = annotationCombineRepository;
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
  protected List<Integer> doBiz(int userId, int role, ResetAnnotationBlockRequest request) {
    final AnnotationBlockActionEnum action = AnnotationBlockActionEnum.valueOf(request.getAction());

    final List<AnnotationTaskBlock> blocks =
        annotationTaskBlockRepository.findAllByStateInAndIdIn(
            Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.FINISHED),
            request.getIds());

    if (blocks.size() != request.getIds().size()) {
      log.warn("语料重新标注中存在非法ID: {}", request.getIds());
    }

    return blocks
        .stream()
        .map(
            block -> {
              block.setState(AnnotationTaskState.DOING);
              blockService.updateTaskAndDocState(annotationTaskBlockRepository.save(block));
              return annotationCombineRepository
                  .save(createAnnotationCombine(action, block))
                  .getId();
            })
        .collect(Collectors.toList());
  }

  private AnnotationCombine createAnnotationCombine(
      final AnnotationBlockActionEnum action, final AnnotationTaskBlock block) {
    final AnnotationCombine annotationCombine = new AnnotationCombine();
    annotationCombine.setTerm(block.getText());
    annotationCombine.setAnnotationType(block.getAnnotationType().ordinal());
    annotationCombine.setAssignee(0);
    annotationCombine.setManualAnnotation(block.getAnnotation());
    annotationCombine.setFinalAnnotation(block.getAnnotation());
    annotationCombine.setReviewedAnnotation(block.getAnnotation());
    annotationCombine.setState(
        action == AnnotationBlockActionEnum.RE_ANNOTATION
            ? AnnotationCombineStateEnum.unDistributed.name()
            : AnnotationCombineStateEnum.preExamine.name());
    return annotationCombine;
  }
}
