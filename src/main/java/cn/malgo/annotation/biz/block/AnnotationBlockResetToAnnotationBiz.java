package cn.malgo.annotation.biz.block;

import cn.malgo.annotation.annotation.RequireRole;
import cn.malgo.annotation.biz.base.TransactionalBiz;
import cn.malgo.annotation.dao.AnnotationCombineRepository;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskDocBlockRepository;
import cn.malgo.annotation.dao.AnnotationTaskDocRepository;
import cn.malgo.annotation.dao.AnnotationTaskRepository;
import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.AnnotationTaskDocBlock;
import cn.malgo.annotation.enums.AnnotationBlockActionEnum;
import cn.malgo.annotation.enums.AnnotationCombineStateEnum;
import cn.malgo.annotation.enums.AnnotationRoleStateEnum;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.request.block.ResetAnnotationBlockRequest;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequireRole(AnnotationRoleStateEnum.admin)
public class AnnotationBlockResetToAnnotationBiz
    extends TransactionalBiz<ResetAnnotationBlockRequest, Object> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;
  private final AnnotationCombineRepository annotationCombineRepository;
  private final AnnotationTaskDocBlockRepository annotationTaskDocBlockRepository;
  private final AnnotationTaskDocRepository annotationTaskDocRepository;
  private final AnnotationTaskRepository annotationTaskRepository;

  public AnnotationBlockResetToAnnotationBiz(
      AnnotationTaskBlockRepository annotationTaskBlockRepository,
      AnnotationCombineRepository annotationCombineRepository,
      AnnotationTaskDocBlockRepository annotationTaskDocBlockRepository,
      AnnotationTaskDocRepository annotationTaskDocRepository,
      AnnotationTaskRepository annotationTaskRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
    this.annotationCombineRepository = annotationCombineRepository;
    this.annotationTaskDocBlockRepository = annotationTaskDocBlockRepository;
    this.annotationTaskDocRepository = annotationTaskDocRepository;
    this.annotationTaskRepository = annotationTaskRepository;
  }

  @Override
  protected void validateRequest(ResetAnnotationBlockRequest resetAnnotationBlockRequest)
      throws InvalidInputException {
    if (resetAnnotationBlockRequest == null) {
      throw new InvalidInputException("invalid-request", "无效的请求");
    }
    if (resetAnnotationBlockRequest.getIdList() == null
        || resetAnnotationBlockRequest.getIdList().size() == 0) {
      throw new InvalidInputException("id-list-empty", "参数blockId集合为空");
    }
    if (StringUtils.isBlank(resetAnnotationBlockRequest.getAction().name())) {
      throw new InvalidInputException("invalid-action", "参数action为空");
    }
  }

  @Override
  protected Object doBiz(
      int userId, int role, ResetAnnotationBlockRequest resetAnnotationBlockRequest) {
    List<AnnotationTaskBlock> annotationTaskBlockList =
        annotationTaskBlockRepository.findAllByIdIn(resetAnnotationBlockRequest.getIdList());
    if (annotationTaskBlockList.size() > 0) {
      // 修改查询出的block的状态为doing
      annotationTaskBlockList
          .stream()
          .forEach(annotationTaskBlock -> multipleUpdateByAnnotationTaskBlock(annotationTaskBlock));
      annotationTaskBlockRepository.saveAll(annotationTaskBlockList);
      // 重新生生成对应的annotationCombine
      addAnnotationCombineByBlocks(
          annotationTaskBlockList, resetAnnotationBlockRequest.getAction());
    }
    return null;
  }

  private void multipleUpdateByAnnotationTaskBlock(AnnotationTaskBlock annotationTaskBlock) {
    annotationTaskBlock.setState(AnnotationTaskState.DOING);
    // 查询出所有的annotationTaskDoc中间表
    List<AnnotationTaskDocBlock> taskDocBlocks =
        annotationTaskDocBlockRepository.findByBlockEquals(annotationTaskBlock);
    // 更新中间表annotationTaskDoc状态,annotationTaskDoc表的状态是由对应的block表决定的，只有对应所有的block recode的状态都为某一状态才更新
    // (换句话说)每次取最小ordinal状态的值更新
    taskDocBlocks.forEach(
        taskDocBlock -> annotationTaskDocRepository.updateState(taskDocBlock.getTaskDoc()));
    // 同时更新task，更新规则同上
    taskDocBlocks
        .stream()
        .map(taskDocBlock -> taskDocBlock.getTaskDoc().getTask())
        .collect(Collectors.toSet())
        .forEach(annotationTaskRepository::updateState);
  }

  private void addAnnotationCombineByBlocks(
      List<AnnotationTaskBlock> annotationTaskBlocks,
      AnnotationBlockActionEnum annotationBlockActionEnum) {
    final List<AnnotationCombine> annotationCombines =
        annotationTaskBlocks
            .stream()
            .map(
                annotationTaskBlock -> {
                  AnnotationCombine annotationCombine = new AnnotationCombine();
                  annotationCombine.setTerm(annotationTaskBlock.getText());
                  annotationCombine.setIsTask(0);
                  annotationCombine.setAnnotationType(
                      annotationTaskBlock.getAnnotationType().ordinal());
                  annotationCombine.setAssignee(0);
                  if (annotationTaskBlock.getAnnotationType() == AnnotationTypeEnum.wordPos) {
                    annotationCombine.setManualAnnotation(annotationTaskBlock.getAnnotation());
                    if (annotationBlockActionEnum
                        .name()
                        .equals(AnnotationBlockActionEnum.REANNOTATION.name())) {
                      annotationCombine.setState(AnnotationCombineStateEnum.unDistributed.name());
                    }
                    if (annotationBlockActionEnum
                        .name()
                        .equals(AnnotationBlockActionEnum.REEXAMINE.name())) {
                      annotationCombine.setState(AnnotationCombineStateEnum.preExamine.name());
                    }
                  } else {
                    if (annotationBlockActionEnum
                        .name()
                        .equals(AnnotationBlockActionEnum.REANNOTATION.name())) {
                      annotationCombine.setState(AnnotationCombineStateEnum.unDistributed.name());
                      annotationCombine.setFinalAnnotation(annotationTaskBlock.getAnnotation());
                    }
                    if (annotationBlockActionEnum
                        .name()
                        .equals(AnnotationBlockActionEnum.REEXAMINE.name())) {
                      annotationCombine.setState(AnnotationCombineStateEnum.preExamine.name());
                      annotationCombine.setReviewedAnnotation(annotationTaskBlock.getAnnotation());
                    }
                  }
                  return annotationCombine;
                })
            .collect(Collectors.toList());
    if (annotationCombines != null && annotationCombines.size() > 0) {
      annotationCombineRepository.saveAll(annotationCombines);
    }
  }
}
