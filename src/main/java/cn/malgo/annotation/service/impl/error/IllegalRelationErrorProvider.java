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
import cn.malgo.annotation.utils.AnnotationDocumentManipulator;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.RelationEntity;
import cn.malgo.core.definition.brat.BratPosition;
import cn.malgo.service.exception.InvalidInputException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IllegalRelationErrorProvider extends BaseErrorProvider {

  private final RelationLimitRuleRepository relationLimitRuleRepository;
  private static final String CRON_STR = "0 0/1 * * * ?";
  private static List<WordErrorWithPosition> globalWordErrorWithPositions;
  private static List<Annotation> globalAnnotations;

  public IllegalRelationErrorProvider(
      final AnnotationFixLogRepository annotationFixLogRepository,
      final RelationLimitRuleRepository relationLimitRuleRepository) {
    super(annotationFixLogRepository);

    this.relationLimitRuleRepository = relationLimitRuleRepository;
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
    // 先过滤出非法规则的标注
    List<WordErrorWithPosition> illegalWordErrors =
        annotations
            .stream()
            .filter(annotation -> annotation.getAnnotationType() == AnnotationTypeEnum.relation)
            .flatMap(annotation -> getIllegalRelations(legalRules, annotation))
            .collect(Collectors.toList());
    globalAnnotations = annotations;
    // 过滤出实体双向关联的标注
    if (globalWordErrorWithPositions == null) {
      globalWordErrorWithPositions =
          getBidirectionalRelationEntity(annotations).collect(Collectors.toList());
    }
    illegalWordErrors.addAll(globalWordErrorWithPositions);
    return postProcess(illegalWordErrors, 0);
  }

  @Scheduled(cron = CRON_STR)
  public void updateBidirectionalWordErrorWithPositions() {
    if (globalAnnotations != null) {
      globalWordErrorWithPositions =
          getBidirectionalRelationEntity(globalAnnotations).collect(Collectors.toList());
    }
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

  private List<RelationPair> getTotalRelationPairs(final List<Annotation> annotations) {
    final List<RelationPair> totalRelationPairs = new ArrayList<>();
    for (int k = 0; k < annotations.size(); k++) {
      final Map<String, Entity> entityMap = annotations.get(k).getDocument().getEntityMap();
      int finalK = k;
      final List<RelationPair> relationPairs =
          annotations
              .get(k)
              .getDocument()
              .getRelationEntities()
              .parallelStream()
              .map(
                  relationEntity ->
                      new RelationPair(
                          relationEntity.getTag(),
                          entityMap.get(relationEntity.getSourceTag()).getTerm(),
                          entityMap.get(relationEntity.getSourceTag()).getType(),
                          entityMap.get(relationEntity.getSourceTag()).getStart(),
                          entityMap.get(relationEntity.getSourceTag()).getEnd(),
                          entityMap.get(relationEntity.getTargetTag()).getTerm(),
                          entityMap.get(relationEntity.getTargetTag()).getType(),
                          entityMap.get(relationEntity.getTargetTag()).getStart(),
                          entityMap.get(relationEntity.getTargetTag()).getEnd(),
                          relationEntity.getType(),
                          annotations.get(finalK)))
              .collect(Collectors.toList());
      totalRelationPairs.addAll(relationPairs);
    }
    return totalRelationPairs;
  }

  private Stream<WordErrorWithPosition> getBidirectionalRelationEntity(
      final List<Annotation> annotations) {
    final Map<String, List<RelationPair>> relationMap =
        getTotalRelationPairs(annotations)
            .parallelStream()
            .collect(Collectors.groupingBy(RelationPair::getRelationType));
    return relationMap
        .entrySet()
        .parallelStream()
        .flatMap(
            entry -> {
              if (entry.getValue().size() > 1) {
                final Set<String> typeSet = new HashSet<>();
                return entry
                    .getValue()
                    .stream()
                    .filter(
                        relationPair -> {
                          final Pair<String, String> sourcePair =
                              Pair.of(
                                  relationPair.getSourceTerm(),
                                  relationPair.getSourceType().replace("-and", ""));
                          final Pair<String, String> targetPair =
                              Pair.of(
                                  relationPair.getTargetTerm(),
                                  relationPair.getTargetType().replace("-and", ""));
                          if (sourcePair.getLeft().equals(targetPair.getLeft())
                              && sourcePair.getRight().equals(targetPair.getRight())) {
                            return false;
                          }
                          return relationMap
                              .entrySet()
                              .parallelStream()
                              .filter(totalEntry -> totalEntry.getKey().equals(entry.getKey()))
                              .flatMap(stringListEntry -> stringListEntry.getValue().stream())
                              .anyMatch(
                                  current ->
                                      current.getTargetTerm().equals(sourcePair.getLeft())
                                          && current
                                              .getTargetType()
                                              .replace("-and", "")
                                              .equals(sourcePair.getRight())
                                          && current.getSourceTerm().equals(targetPair.getLeft())
                                          && current
                                              .getSourceType()
                                              .replace("-and", "")
                                              .equals(targetPair.getRight()));
                        })
                    .map(
                        relationPair -> {
                          //                          log.info(
                          //                              "id:{},rTag:{}
                          // ,sourceTerm:{},sourceType:{},relationType:{},targetTerm:{},targetType:{}",
                          //                              relationPair.annotation.getId(),
                          //                              relationPair.getRelationTag(),
                          //                              relationPair.getSourceTerm(),
                          //                              relationPair.getSourceType(),
                          //                              relationPair.getRelationType(),
                          //                              relationPair.getTargetTerm(),
                          //                              relationPair.getTargetType()
                          //                          );
                          String term =
                              String.format(
                                  "[%s,%s] -> [%s,%s]",
                                  relationPair.getSourceTerm(),
                                  relationPair.getSourceType().replace("-and", ""),
                                  relationPair.getTargetTerm(),
                                  relationPair.getTargetType().replace("-and", ""));
                          if (typeSet.add(term)
                              && typeSet
                                  .stream()
                                  .anyMatch(
                                      s ->
                                          s.equals(
                                              String.format(
                                                  "[%s,%s] -> [%s,%s]",
                                                  relationPair.getTargetTerm(),
                                                  relationPair.getTargetType().replace("-and", ""),
                                                  relationPair.getSourceTerm(),
                                                  relationPair
                                                      .getSourceType()
                                                      .replace("-and", ""))))) {
                            typeSet.remove(term);
                            term =
                                String.format(
                                    "[%s,%s] -> [%s,%s]",
                                    relationPair.getTargetTerm(),
                                    relationPair.getTargetType(),
                                    relationPair.getSourceTerm(),
                                    relationPair.getSourceType());
                          }
                          return new WordErrorWithPosition(
                              term,
                              relationPair.getRelationType(),
                              new BratPosition(
                                  Math.min(
                                      relationPair.getSourceStart(), relationPair.getTargetStart()),
                                  Math.max(
                                      relationPair.getSourceEnd(), relationPair.getTargetEnd())),
                              relationPair.getAnnotation(),
                              relationPair.getRelationTag());
                        });
              } else {
                return Stream.empty();
              }
            });
  }

  @lombok.Value
  static class RelationPair {

    private final String relationTag;
    private final String sourceTerm;
    private final String sourceType;
    private final int sourceStart;
    private final int sourceEnd;
    private final String targetTerm;
    private final String targetType;
    private final int targetStart;
    private final int targetEnd;
    private final String relationType;
    private final Annotation annotation;
  }

  @lombok.Value
  static class RelationLimitRulePair {

    private final String source;
    private final String target;
    private final String relation;
  }
}
