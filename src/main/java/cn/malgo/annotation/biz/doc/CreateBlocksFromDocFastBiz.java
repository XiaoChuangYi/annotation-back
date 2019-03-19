package cn.malgo.annotation.biz.doc;

import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.request.task.CreateBlocksFromDocRequest;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateBlocksFromDocFastBiz
    extends TransactionalBiz<CreateBlocksFromDocRequest, Object> {

  private final OriginalDocRepository originalDocRepository;
  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public CreateBlocksFromDocFastBiz(
      final OriginalDocRepository originalDocRepository,
      final AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.originalDocRepository = originalDocRepository;
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(CreateBlocksFromDocRequest request) throws InvalidInputException {
    if (request.getDocIds() == null || request.getDocIds().size() == 0) {
      throw new InvalidInputException("invalid-doc-ids", "doc ids should have value");
    }

    if (request.getAnnotationType() < 0
        || request.getAnnotationType() >= AnnotationTypeEnum.values().length) {
      throw new InvalidInputException(
          "invalid-annotation-type", "invalid annotation type: " + request.getAnnotationType());
    }
  }

  @Override
  protected Object doBiz(CreateBlocksFromDocRequest request) {
    final AnnotationTypeEnum annotationType =
        AnnotationTypeEnum.getByValue(request.getAnnotationType());

    List<OriginalDoc> docs = originalDocRepository.findAllById(request.getDocIds());
    if (docs.size() != request.getDocIds().size()) {
      log.warn("some doc ids are invalid {}", request.getDocIds());
    }
    final List<AnnotationTaskBlock> annotationTaskBlocks =
        docs.parallelStream()
            .map(
                originalDoc ->
                    new AnnotationTaskBlock(
                        originalDoc.getText(),
                        "",
                        "",
                        0d,
                        AnnotationTaskState.CREATED,
                        annotationType,
                        0L,
                        ""))
            .collect(Collectors.toList());
    if (docs.size() > 0) {
      docs.parallelStream()
          .forEach(
              originalDoc -> {
                originalDoc.setState(OriginalDocState.PROCESSING);
                final List<AnnotationTaskBlock> docBlocks =
                    getSpecificDocBlocks(originalDoc.getText(), annotationTaskBlocks);
                docBlocks.stream().forEach(docBlock -> originalDoc.addBlock(docBlock, 1));
              });
    }
    annotationTaskBlockRepository.saveAll(annotationTaskBlocks);
    originalDocRepository.saveAll(docs);
    return true;
  }

  private List<AnnotationTaskBlock> getSpecificDocBlocks(
      String docText, List<AnnotationTaskBlock> annotationTaskBlocks) {
    List<AnnotationTaskBlock> blocks =
        annotationTaskBlocks
            .parallelStream()
            .filter(annotationTaskBlock -> annotationTaskBlock.getText().equals(docText))
            .collect(Collectors.toList());
    return blocks;
  }
}
