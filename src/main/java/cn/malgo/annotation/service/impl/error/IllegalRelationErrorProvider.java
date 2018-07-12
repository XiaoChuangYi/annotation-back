package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.WordTypeCount;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.FixAnnotationErrorContext;
import cn.malgo.annotation.dto.error.FixAnnotationErrorData;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.entity.RelationLimitRule;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.annotation.exception.InvalidInputException;
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.annotation.utils.entity.RelationEntity;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class IllegalRelationErrorProvider extends BaseErrorProvider {
  private final RelationLimitRuleRepository relationLimitRuleRepository;
  private final int batchSize;

  public IllegalRelationErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository,
      final RelationLimitRuleRepository relationLimitRuleRepository,
      @Value("${malgo.annotation.fix-log-batch-size}") final int batchSize) {
    super(annotationFixLogRepository);

    this.relationLimitRuleRepository = relationLimitRuleRepository;
    this.batchSize = batchSize;
  }

  @Override
  public AnnotationErrorEnum getErrorEnum() {
    return AnnotationErrorEnum.ILLEGAL_RELATION;
  }

  @Override
  public List<AlgorithmAnnotationWordError> find(final List<Annotation> annotations) {
    log.info("start finding illegal relations");

    final List<RelationLimitRule> rules = relationLimitRuleRepository.findAll();
    final Set<RelationLimitRulePair> legalRules =
        rules
            .stream()
            .map(
                rule ->
                    new RelationLimitRulePair(
                        rule.getSource(), rule.getTarget(), rule.getRelationType()))
            .collect(Collectors.toSet());

    return postProcess(
        annotations
            .stream()
            .filter(annotation -> annotation.getAnnotationType() == AnnotationTypeEnum.relation)
            .flatMap(annotation -> getIllegalRelations(legalRules, annotation))
            .collect(Collectors.toList()),
        this.batchSize);
  }

  @Override
  protected AlgorithmAnnotationWordError getWordError(final String word) {
    final String[] sourceAndTarget = word.split(" -> ");
    if (sourceAndTarget.length == 2) {
      return new AlgorithmAnnotationWordError(
          word,
          relationLimitRuleRepository
              .findBySourceAndTarget(sourceAndTarget[0], sourceAndTarget[1])
              .stream()
              .map(
                  relationLimitRule -> new WordTypeCount(relationLimitRule.getRelationType(), 0, 0))
              .collect(Collectors.toList()));
    }

    return super.getWordError(word);
  }

  @Override
  public List<Entity> fix(
      final Annotation annotation,
      final FixAnnotationErrorContext context,
      final FixAnnotationErrorData data)
      throws InvalidInputException {
    final FixAnnotationErrorData.IllegalRelationRepairData repairData =
        data.getIllegalRelationRepair();

    if (repairData == null || context.getInfo() == null || !(context.getInfo() instanceof String)) {
      throw new InvalidInputException("invalid-fix-relations", "非法关系修复数据为空");
    }

    if (StringUtils.isBlank(repairData.getType()) && !repairData.isReverse()) {
      throw new InvalidInputException("invalid-fix-relations", "非法关系修复数据不合法");
    }

    final AnnotationDocument document = annotation.getDocument();
    final RelationEntity relationEntity = document.getRelation((String) context.getInfo());
    if (relationEntity == null) {
      throw new InvalidInputException("invalid-fix-relations", "找不到对应的relation");
    }

    if (StringUtils.isNotBlank(repairData.getType())) {
      relationEntity.setType(repairData.getType());
    }

    if (repairData.isReverse()) {
      final String sourceTag = relationEntity.getSourceTag();
      relationEntity.setSourceTag(relationEntity.getTargetTag());
      relationEntity.setTargetTag(sourceTag);
    }

    annotation.setAnnotation(AnnotationDocumentManipulator.toBratAnnotations(document));
    return Collections.emptyList();
  }

  @NotNull
  private Stream<WordErrorWithPosition> getIllegalRelations(
      final Set<RelationLimitRulePair> legalRules, final Annotation annotation) {
    final Map<String, Entity> entityMap = annotation.getDocument().getEntityMap();

    return annotation
        .getDocument()
        .getRelationEntities()
        .stream()
        .filter(
            relation ->
                !legalRules.contains(
                    new RelationLimitRulePair(
                        entityMap.get(relation.getSourceTag()).getType().replace("-and", ""),
                        entityMap.get(relation.getTargetTag()).getType().replace("-and", ""),
                        relation.getType())))
        .map(
            relation ->
                new WordErrorWithPosition(
                    entityMap.get(relation.getSourceTag()).getType()
                        + " -> "
                        + entityMap.get(relation.getTargetTag()).getType(),
                    relation.getType(),
                    new BratPosition(
                        Math.min(
                            entityMap.get(relation.getSourceTag()).getStart(),
                            entityMap.get(relation.getTargetTag()).getStart()),
                        Math.max(
                            entityMap.get(relation.getSourceTag()).getEnd(),
                            entityMap.get(relation.getTargetTag()).getEnd())),
                    annotation,
                    relation.getTag()));
  }

  @lombok.Value
  static class RelationLimitRulePair {
    private final String source;
    private final String target;
    private final String relation;
  }
}
