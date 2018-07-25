package cn.malgo.annotation.biz.task;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.OriginalDocRepository;
import cn.malgo.annotation.entity.OriginalDoc;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.enums.OriginalDocState;
import cn.malgo.annotation.request.task.CreateBlocksFromDocRequest;
import cn.malgo.annotation.service.OriginalDocService;
import cn.malgo.annotation.vo.CreateBlocksFromDocVO;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.TransactionalBiz;
import cn.malgo.service.exception.InvalidInputException;
import cn.malgo.service.model.UserDetails;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequirePermission(Permissions.ADMIN)
public class CreateBlocksFromDocBiz
    extends TransactionalBiz<CreateBlocksFromDocRequest, CreateBlocksFromDocVO> {
  private final OriginalDocRepository docRepository;
  private final OriginalDocService originalDocService;

  public CreateBlocksFromDocBiz(
      final OriginalDocRepository docRepository, final OriginalDocService originalDocService) {
    this.docRepository = docRepository;
    this.originalDocService = originalDocService;
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
  protected CreateBlocksFromDocVO doBiz(
      final CreateBlocksFromDocRequest request, final UserDetails user) {
    final AnnotationTypeEnum annotationType =
        AnnotationTypeEnum.getByValue(request.getAnnotationType());

    List<OriginalDoc> docs = docRepository.findAllById(request.getDocIds());
    if (docs.size() != request.getDocIds().size()) {
      log.warn("some doc ids are invalid {}", request.getDocIds());
    }

    int createdBlocks = 0;
    for (final OriginalDoc doc : docs) {
      log.info("create blocks from doc({}), annotation type: {}", doc.getId(), annotationType);
      final Pair<OriginalDoc, Integer> createBlocksResult =
          originalDocService.createBlocks(doc, annotationType);
      log.info(
          "create blocks from doc({}) success, blocks: {}",
          doc.getId(),
          createBlocksResult.getRight());
      createdBlocks += createBlocksResult.getRight();
      doc.setState(OriginalDocState.PROCESSING);
    }

    if (docs.size() != 0) {
      docs = docRepository.saveAll(docs);
    }

    return new CreateBlocksFromDocVO(docs, createdBlocks);
  }
}
