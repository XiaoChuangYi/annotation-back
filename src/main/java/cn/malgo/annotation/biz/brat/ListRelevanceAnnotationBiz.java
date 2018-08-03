package cn.malgo.annotation.biz.brat;

import cn.malgo.annotation.constants.Permissions;
import cn.malgo.annotation.dao.AnnotationTaskBlockRepository;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.enums.AnnotationTaskState;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.request.block.ListRelevanceAnnotationRequest;
import cn.malgo.annotation.result.PageVO;
import cn.malgo.annotation.utils.AnnotationConvert;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.annotation.vo.RelationSearchResponse;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import cn.malgo.service.annotation.RequirePermission;
import cn.malgo.service.biz.BaseBiz;
import cn.malgo.service.exception.InvalidInputException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequirePermission(Permissions.ADMIN)
@Slf4j
public class ListRelevanceAnnotationBiz
    extends BaseBiz<ListRelevanceAnnotationRequest, PageVO<RelationSearchResponse>> {

  private final AnnotationTaskBlockRepository annotationTaskBlockRepository;

  public ListRelevanceAnnotationBiz(AnnotationTaskBlockRepository annotationTaskBlockRepository) {
    this.annotationTaskBlockRepository = annotationTaskBlockRepository;
  }

  @Override
  protected void validateRequest(ListRelevanceAnnotationRequest listRelevanceAnnotationRequest)
      throws InvalidInputException {
    if (listRelevanceAnnotationRequest.getPageIndex() <= 0) {
      throw new InvalidInputException("invalid-page-index", "无效的pageIndex");
    }

    if (listRelevanceAnnotationRequest.getPageSize() <= 0) {
      throw new InvalidInputException("invalid-page-size", "无效的pageSize");
    }
  }

  @Override
  protected PageVO<RelationSearchResponse> doBiz(ListRelevanceAnnotationRequest request) {
    final Set<AnnotationTaskBlock> blocks =
        annotationTaskBlockRepository.findByAnnotationTypeEqualsAndStateIn(
            AnnotationTypeEnum.relation,
            Arrays.asList(AnnotationTaskState.ANNOTATED, AnnotationTaskState.FINISHED));
    final List<RelationQueryPair> relations = getRelationQueryPairs(blocks, request);
    final int skip = (request.getPageIndex() - 1) * request.getPageSize();
    final int limit = request.getPageSize();
    final PageVO<RelationSearchResponse> pageVO = new PageVO<>();
    pageVO.setTotal(relations.size());
    pageVO.setDataList(
        relations
            .stream()
            .skip(skip)
            .limit(limit)
            .map(
                relation ->
                    new RelationSearchResponse(
                        AnnotationConvert.convert2AnnotationBlockBratVO(relation.getBlock()),
                        relation.bratPosition))
            .collect(Collectors.toList()));
    return pageVO;
  }

  private List<RelationQueryPair> getRelationQueryPairs(
      Set<AnnotationTaskBlock> blocks, ListRelevanceAnnotationRequest request) {
    return blocks
        .stream()
        .map(BlockDocument::new)
        .flatMap(
            pair -> {
              final AnnotationDocument document = pair.getDocument();
              final Map<String, Entity> entityMap = document.getEntityMap();
              return document
                  .getRelationEntities()
                  .stream()
                  .map(
                      relationEntity ->
                          new RelationQueryPair(
                              pair.getBlock(),
                              entityMap.get(relationEntity.getSourceTag()).getTerm(),
                              entityMap.get(relationEntity.getSourceTag()).getType(),
                              entityMap.get(relationEntity.getTargetTag()).getTerm(),
                              entityMap.get(relationEntity.getTargetTag()).getType(),
                              relationEntity.getType(),
                              new BratPosition(
                                  Math.min(
                                      entityMap.get(relationEntity.getSourceTag()).getStart(),
                                      entityMap.get(relationEntity.getTargetTag()).getStart()),
                                  Math.max(
                                      entityMap.get(relationEntity.getSourceTag()).getEnd(),
                                      entityMap.get(relationEntity.getTargetTag()).getEnd()))))
                  .filter(relation -> relation.matches(request));
            })
        .collect(Collectors.toList());
  }

  @lombok.Value
  static class RelationQueryPair {

    private final AnnotationTaskBlock block;
    private final String sourceTerm;
    private final String sourceType;
    private final String targetTerm;
    private final String targetType;
    private final String relation;
    private final BratPosition bratPosition;

    boolean matches(ListRelevanceAnnotationRequest request) {
      return (StringUtils.isBlank(request.getRelation())
              || StringUtils.equalsIgnoreCase(relation, request.getRelation()))
          && (StringUtils.isBlank(request.getSourceType())
              || StringUtils.equalsIgnoreCase(sourceType, request.getSourceType()))
          && (StringUtils.isBlank(request.getTargetType())
              || StringUtils.equalsIgnoreCase(targetType, request.getTargetType()))
          && (StringUtils.isBlank(request.getSourceText())
              || StringUtils.equalsIgnoreCase(sourceTerm, request.getSourceText()))
          && (StringUtils.isBlank(request.getTargetText())
              || StringUtils.equalsIgnoreCase(targetTerm, request.getTargetText()));
    }
  }

  @lombok.Value
  static class BlockDocument {

    private final AnnotationTaskBlock block;
    private final AnnotationDocument document;

    BlockDocument(AnnotationTaskBlock block) {
      this.block = block;
      this.document = new AnnotationDocument(block.getText());
      AnnotationDocumentManipulator.parseBratAnnotation(block.getAnnotation(), this.document);
    }
  }
}
