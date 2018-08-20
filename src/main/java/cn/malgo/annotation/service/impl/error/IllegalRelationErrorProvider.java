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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IllegalRelationErrorProvider extends BaseErrorProvider {

  private final RelationLimitRuleRepository relationLimitRuleRepository;

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
        rules.stream().map(RelationLimitRulePair::new).collect(Collectors.toSet());

    // 先过滤出非法规则的标注
    final List<WordErrorWithPosition> illegalRelations =
        annotations
            .stream()
            .filter(annotation -> annotation.getAnnotationType() == AnnotationTypeEnum.relation)
            .flatMap(annotation -> getIllegalRelations(legalRules, annotation))
            .collect(Collectors.toList());

    // 为了让双向类型的错误永远在非法关联之后
    final List<AlgorithmAnnotationWordError> result = postProcess(illegalRelations, 0);
    result.addAll(postProcess(getIllegalBidirectionalRelations(annotations), 0));
    return result;
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
            relation -> {
              if (StringUtils.equalsAny(relation.getType(), "and", "coreference")
                  && entityMap.get(relation.getSourceTag()).getEnd()
                      <= entityMap.get(relation.getTargetTag()).getStart()) {
                return false;
              }
              return !legalRules.contains(
                  new RelationLimitRulePair(
                      entityMap.get(relation.getSourceTag()).getType().replace("-and", ""),
                      entityMap.get(relation.getTargetTag()).getType().replace("-and", ""),
                      relation.getType()));
            })
        .map(
            relation -> {
              final Entity source = entityMap.get(relation.getSourceTag());
              final Entity target = entityMap.get(relation.getTargetTag());
              return new WordErrorWithPosition(
                  source.getType() + " -> " + target.getType(),
                  relation.getType(),
                  getRelationPosition(source, target),
                  annotation,
                  relation.getTag());
            });
  }

  @NotNull
  private BratPosition getRelationPosition(final Entity lhs, final Entity rhs) {
    return new BratPosition(
        Math.min(lhs.getStart(), rhs.getStart()), Math.max(lhs.getEnd(), rhs.getEnd()));
  }

  private List<WordErrorWithPosition> getIllegalBidirectionalRelations(
      final List<Annotation> annotations) {
    return annotations
        .parallelStream()
        .flatMap(this::getUniqueRelations)
        .collect(Collectors.groupingBy(Pair::getLeft))
        .entrySet()
        .parallelStream()
        .filter(this::isOneDirectionRelation)
        .flatMap(entry -> entry.getValue().parallelStream().map(Pair::getRight))
        .collect(Collectors.toList());
  }

  private boolean isOneDirectionRelation(
      final Map.Entry<RelationUniqueTerm, List<Pair<RelationUniqueTerm, WordErrorWithPosition>>>
          entry) {
    return entry
            .getValue()
            .parallelStream()
            .map(p -> p.getRight().getType())
            .collect(Collectors.toSet())
            .size()
        != 1;
  }

  private static final String[] belongToSpecialTerms = {"表面", "切面"};

  private boolean filterSpecificType(RelationEntity relationEntity, Map<String, Entity> entityMap) {
    if (StringUtils.equalsAny(relationEntity.getType(), "and", "coreference")) {
      return false;
    }
    final Entity source = entityMap.get(relationEntity.getSourceTag());
    final Entity target = entityMap.get(relationEntity.getTargetTag());
    if (StringUtils.equalsAny(relationEntity.getType(), "belong-to")
        && StringUtils.equalsAny(source.getType(), "Clinical-finding", "Observable-entity")
        && StringUtils.equalsAny(target.getType(), "Clinical-finding", "Observable-entity")) {
      if (source.getType().equals("Observable-entity")
          && Arrays.asList(belongToSpecialTerms).contains(source.getTerm())) {
        return false;
      } else if (target.getType().equals("Observable-entity")
          && Arrays.asList(belongToSpecialTerms).contains(source.getTerm())) {
        return false;
      } else {
        return true;
      }
    }
    return true;
  }

  @NotNull
  private Stream<Pair<RelationUniqueTerm, WordErrorWithPosition>> getUniqueRelations(
      final Annotation annotation) {
    final Map<String, Entity> entityMap = annotation.getDocument().getEntityMap();
    return annotation
        .getDocument()
        .getRelationEntities()
        .parallelStream()
        .filter(relationEntity -> filterSpecificType(relationEntity, entityMap))
        .map(
            relation -> {
              final Entity source = entityMap.get(relation.getSourceTag());
              final Entity target = entityMap.get(relation.getTargetTag());

              final RelationUniqueTerm uniqueRelation =
                  new RelationUniqueTerm(source, target, relation);
              return Pair.of(
                  uniqueRelation,
                  new WordErrorWithPosition(
                      String.format(
                          "%s: %s(%s) -> %s(%s)",
                          uniqueRelation.getRelation(),
                          uniqueRelation.getLhsTerm(),
                          uniqueRelation.getLhsType(),
                          uniqueRelation.getRhsTerm(),
                          uniqueRelation.getRhsType()),
                      source.getTerm().compareToIgnoreCase(target.getTerm()) < 0 ? "正向" : "反向",
                      getRelationPosition(source, target),
                      annotation,
                      relation.getTag()));
            });
  }

  /** 如果两个关系的source/target的类型、文本都一致，而且relation也是一致的，则认为是相同类似的关联，则不允许出现两个方向的关联 */
  @lombok.Value
  static class RelationUniqueTerm {

    private String lhsTerm;
    private String lhsType;
    private String rhsTerm;
    private String rhsType;
    private String relation;

    RelationUniqueTerm(final Entity source, final Entity target, final RelationEntity relation) {
      if (source.getTerm().compareToIgnoreCase(target.getTerm()) < 0) {
        this.lhsTerm = StringUtils.lowerCase(source.getTerm());
        this.lhsType = source.getType().replace("-and", "");
        this.rhsTerm = StringUtils.lowerCase(target.getTerm());
        this.rhsType = target.getType().replace("-and", "");
      } else {
        this.lhsTerm = StringUtils.lowerCase(target.getTerm());
        this.lhsType = target.getType().replace("-and", "");
        this.rhsTerm = StringUtils.lowerCase(source.getTerm());
        this.rhsType = source.getType().replace("-and", "");
      }

      this.relation = relation.getType();
    }
  }

  @lombok.Value
  @AllArgsConstructor
  static class RelationLimitRulePair {

    private final String source;
    private final String target;
    private final String relation;

    RelationLimitRulePair(final RelationLimitRule rule) {
      this.source = rule.getSource();
      this.target = rule.getTarget();
      this.relation = rule.getRelationType();
    }
  }
}
