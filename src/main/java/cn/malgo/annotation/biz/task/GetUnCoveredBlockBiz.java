package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.request.task.GetUnCoveredBlockRequest;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
public class GetUnCoveredBlockBiz
    extends BaseBiz<GetUnCoveredBlockRequest, List<AnnotationTaskBlock>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public GetUnCoveredBlockBiz(final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(GetUnCoveredBlockRequest request) throws InvalidInputException {
    if (request.getPresupposePageSize() > 1000) {
      throw new InvalidInputException("presuppose-page-size-too-large", "预取条数太大");
    }
  }

  @Override
  protected List<AnnotationTaskBlock> doBiz(GetUnCoveredBlockRequest request, UserDetails user) {
    final List<AnnotationTaskBlock> annotationTaskBlocks =
        annotationTaskBlockRepository.findByStateIn(
            Collections.singletonList(AnnotationTaskState.CREATED),
            PageRequest.of(0, request.getPresupposePageSize()));
    final LevenshteinDistance levenshteinDistance = new LevenshteinDistance(request.getThreshold());
    Iterator<AnnotationTaskBlock> iterator = annotationTaskBlocks.iterator();
    while (iterator.hasNext()) {
      final AnnotationTaskBlock annotationTaskBlock = iterator.next();
      if (annotationTaskBlocks
          .stream()
          .anyMatch(
              current ->
                  current.getId() != annotationTaskBlock.getId()
                      && levenshteinDistance.apply(current.getText(), annotationTaskBlock.getText())
                          > 0)) {
        iterator.remove();
      }
    }
    return annotationTaskBlocks;
  }
}
