package cn.malgo.annotation.service.impl.error;

import cn.malgo.annotation.dao.AnnotationFixLogRepository;
import cn.malgo.annotation.dao.RelationLimitRuleRepository;
import cn.malgo.annotation.dto.Annotation;
import cn.malgo.annotation.dto.error.AlgorithmAnnotationWordError;
import cn.malgo.annotation.dto.error.WordErrorWithPosition;
import cn.malgo.annotation.entity.RelationLimitRule;
import cn.malgo.annotation.enums.AnnotationErrorEnum;
import cn.malgo.annotation.enums.AnnotationTypeEnum;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.brat.BratPosition;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
            .flatMap(
                rule ->
                    StringUtils.equals(rule.getSource(), rule.getTarget())
                        ? Stream.of(
                            new RelationLimitRulePair(
                                rule.getSource(), rule.getTarget(), rule.getRelationType()))
                        : Stream.of(
                            new RelationLimitRulePair(
                                rule.getTarget(), rule.getSource(), rule.getRelationType()),
                            new RelationLimitRulePair(
                                rule.getSource(), rule.getTarget(), rule.getRelationType())))
            .collect(Collectors.toSet());

    return postProcess(
        annotations
            .stream()
            .filter(annotation -> annotation.getAnnotationType() == AnnotationTypeEnum.relation)
            .flatMap(annotation -> getIllegalRelations(legalRules, annotation))
            .collect(Collectors.toList()),
        this.batchSize);
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
                        + "-"
                        + entityMap.get(relation.getTargetTag()).getType(),
                    relation.getType(),
                    new BratPosition(
                        Math.min(
                            entityMap.get(relation.getSourceTag()).getStart(),
                            entityMap.get(relation.getTargetTag()).getStart()),
                        Math.max(
                            entityMap.get(relation.getSourceTag()).getEnd(),
                            entityMap.get(relation.getTargetTag()).getEnd())),
                    annotation));
  }

  @lombok.Value
  static class RelationLimitRulePair {
    private final String source;
    private final String target;
    private final String relation;
  }
}
