package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.GetUnCoveredBlockRequest;
import cn.malgo.annotation.vo.AnnotationTaskBlockResponse;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
@Slf4j
public class GetUnCoveredBlockBiz
    extends BaseBiz<GetUnCoveredBlockRequest, List<AnnotationTaskBlockResponse>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public GetUnCoveredBlockBiz(final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(GetUnCoveredBlockRequest request) throws InvalidInputException {
    if (request.getPresupposePageSize() > 1000) {
      throw new InvalidInputException("presuppose-page-size-too-large", "预取条数太大");
    }
    if (request.getThreshold() < 0) {
      throw new InvalidInputException("threshold-must-not-be-negative", "阈值不能为负数");
    }
  }

  @Override
  protected List<AnnotationTaskBlockResponse> doBiz(
      GetUnCoveredBlockRequest request, UserDetails user) {
    final List<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findByStateIn(
            Collections.singletonList(AnnotationTaskState.CREATED),
            PageRequest.of(
                0, request.getPresupposePageSize(), Sort.by(Direction.DESC, "nerFreshRate")));
    final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    for (int k = 0; k < annotationTaskBlocks.size(); k++) {
      final AnnotationTaskBlock current = annotationTaskBlocks.get(k);
      annotationTaskBlocks
          .subList(k + 1, annotationTaskBlocks.size())
          .removeIf(
              block -> {
                final double distance =
                    levenshteinDistance.apply(current.getText(), block.getText())
                        / (double) Math.max(current.getText().length(), block.getText().length());
                return distance < request.getThreshold();
              });
    }
    return annotationTaskBlocks
        .stream()
        .map(block -> new AnnotationTaskBlockResponse(block, false))
        .collect(Collectors.toList());
  }
}
