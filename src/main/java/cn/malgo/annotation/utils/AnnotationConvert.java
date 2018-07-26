package cn.malgo.annotation.utils;

import cn.malgo.annotation.entity.AnnotationCombine;
import cn.malgo.annotation.entity.AnnotationTaskBlock;
import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.annotation.vo.AnnotationBlockBratVO;
import cn.malgo.annotation.vo.AnnotationCombineBratVO;
import cn.malgo.core.definition.BratConst;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.RelationEntity;
import cn.malgo.service.exception.InternalServerException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class AnnotationConvert {

  private static final String SPECIAL_TYPE = "Anchor";

  /** 判断当前标注实体是否交叉 */
  public static boolean isCrossAnnotation(String annotation) {
    final List<Entity> entitiesLeft = getEntitiesFromAnnotation(annotation);
    final List<Entity> entitiesRight = getEntitiesFromAnnotation(annotation);
    // 排除掉自己和自己的比较
    return entitiesLeft
        .stream()
        .anyMatch(
            entityLeft ->
                entitiesRight
                    .stream()
                    .filter(entityRight -> !entityRight.getTag().equals(entityLeft.getTag()))
                    .anyMatch(
                        entityRight -> {
                          final boolean cross =
                              !(entityRight.getStart() >= entityLeft.getEnd()
                                  || entityRight.getEnd() <= entityLeft.getStart());
                          if (cross) {
                            if (entityRight.getStart() == entityLeft.getStart()
                                && entityRight.getEnd() == entityLeft.getEnd()
                                && entityRight.getType().equals(entityLeft.getType())) {
                              return false;
                            } else if (entityRight.getStart() == entityLeft.getStart()
                                && entityRight.getEnd() == entityLeft.getEnd()
                                && StringUtils.equalsAny(
                                    SPECIAL_TYPE, entityRight.getType(), entityLeft.getType())) {
                              return false;
                            } else {
                              return true;
                            }
                          } else {
                            return false;
                          }
                        }));
  }

  /** 获取指定标注的entities */
  public static List<Entity> getEntitiesFromAnnotation(String annotation) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        annotation == null ? "" : annotation, annotationDocument);
    return annotationDocument.getEntities();
  }

  /** 获取指定标注的relationEntities */
  public static List<RelationEntity> getRelationEntitiesFromAnnotation(String annotation) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        annotation == null ? "" : annotation, annotationDocument);
    return annotationDocument.getRelationEntities();
  }

  /** 获取指定的relationEntity */
  public static RelationEntity getRelationEntityFromAnnotation(String annotation, String tag) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        annotation == null ? "" : annotation, annotationDocument);
    return annotationDocument
        .getRelationEntities()
        .stream()
        .filter(x -> x.getTag().equals(tag))
        .findFirst()
        .get();
  }

  /** 查询，将字符串形式的格式转换成前端可以渲染的jsonObject */
  public static JSONObject convertAnnotation2BratFormat(
      String text, String annotation, int annotationType) {
    AnnotationDocument annotationDocument = new AnnotationDocument(text);
    AnnotationDocumentManipulator.parseBratAnnotation(
        annotation == null ? "" : annotation, annotationDocument);
    JSONObject finalJsonObj = AnnotationDocumentManipulator.toBratAjaxFormat(annotationDocument);
    if (annotationType == 1) { // 分句
      List<Integer> endPositionList =
          annotationDocument
              .getEntities()
              .stream()
              .filter(x -> !x.getType().endsWith("-deleted"))
              .map(Entity::getEnd)
              .sorted()
              .collect(Collectors.toList());
      List<Integer> startPositionList = new ArrayList<>(endPositionList);
      if (endPositionList.size() == 0
          || endPositionList.get(endPositionList.size() - 1)
              < annotationDocument.getText().length()) {
        endPositionList.add(annotationDocument.getText().length());
      } else {
        startPositionList.remove(startPositionList.size() - 1);
      }
      if (startPositionList.size() == 0 || startPositionList.get(0) != 0) {

        startPositionList.add(0, 0);
      }
      finalJsonObj.put(
          BratConst.SENTENCE_OFFSET,
          IntStream.range(0, endPositionList.size())
              .mapToObj(i -> Arrays.asList(startPositionList.get(i), endPositionList.get(i)))
              .collect(Collectors.toList()));
    }
    return finalJsonObj;
  }

  public static AnnotationBlockBratVO convert2AnnotationBlockBratVO(
      AnnotationTaskBlock annotationTaskBlock) {
    final JSONObject annotationJson;

    try {
      annotationJson =
          convertAnnotation2BratFormat(
              annotationTaskBlock.getText(),
              annotationTaskBlock.getAnnotation(),
              annotationTaskBlock.getAnnotationType().ordinal());
    } catch (Exception ex) {
      log.info(
          "Brat装换异常,异常标注id：{},对应的文本：{},标注数据：{},异常信息内容：{}",
          annotationTaskBlock.getId(),
          annotationTaskBlock.getText(),
          annotationTaskBlock.getAnnotation(),
          ex.getMessage());
      throw new InternalServerException("异常标注id：" + annotationTaskBlock.getId());
    }

    return new AnnotationBlockBratVO(
        annotationTaskBlock.getId(),
        annotationJson,
        annotationTaskBlock.getAnnotationType().ordinal(),
        annotationTaskBlock.getCreatedTime(),
        annotationTaskBlock.getLastModified(),
        annotationTaskBlock.getState().name(),
        annotationTaskBlock.getText());
  }

  /** 将分词标注数据装载到前端vo对象中 */
  public static AnnotationCombineBratVO convert2AnnotationCombineBratVO(
      AnnotationCombine annotationCombine) {
    JSONObject finalBratJson, reviewedBratJson;
    try {
      finalBratJson =
          convertAnnotation2BratFormat(
              annotationCombine.getTerm(),
              annotationCombine.getFinalAnnotation(),
              annotationCombine.getAnnotationType());
      reviewedBratJson =
          convertAnnotation2BratFormat(
              annotationCombine.getTerm(),
              annotationCombine.getReviewedAnnotation(),
              annotationCombine.getAnnotationType());
    } catch (Exception ex) {
      log.info(
          "Brat装换异常,异常标注id：{},对应的文本：{},标注数据：{},异常信息内容：{}",
          annotationCombine.getId(),
          annotationCombine.getTerm(),
          annotationCombine.getFinalAnnotation() + "--" + annotationCombine.getReviewedAnnotation(),
          ex.getMessage());
      throw new InternalServerException("异常标注id：" + annotationCombine.getId());
    }

    AnnotationCombineBratVO annotationCombineBratVO = new AnnotationCombineBratVO();
    BeanUtils.copyProperties(annotationCombine, annotationCombineBratVO);
    annotationCombineBratVO.setFinalAnnotation(finalBratJson);
    annotationCombineBratVO.setReviewedAnnotation(reviewedBratJson);
    return annotationCombineBratVO;
  }

  public static String addUncomfirmed(String oldAnnotation) {
    AnnotationDocument document = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, document);
    document
        .getEntities()
        .forEach(
            x -> {
              if (!x.getType().endsWith("-unconfirmed")) {
                x.setType(x.getType() + "-unconfirmed");
              }
            });
    return AnnotationDocumentManipulator.toBratAnnotations(document);
  }

  /** 批量将分词标注装载到前端vo对象中 */
  public static List<AnnotationCombineBratVO> convert2AnnotationCombineBratVOList(
      List<AnnotationCombine> annotationCombineList) {
    List<AnnotationCombineBratVO> annotationBratVOList = new LinkedList<>();
    if (annotationCombineList.size() > 0) {
      for (AnnotationCombine annotation : annotationCombineList) {
        JSONObject finalBratJson, reviewedBratJson;
        try {
          reviewedBratJson =
              convertAnnotation2BratFormat(
                  annotation.getTerm(),
                  annotation.getReviewedAnnotation(),
                  annotation.getAnnotationType());
          finalBratJson =
              convertAnnotation2BratFormat(
                  annotation.getTerm(),
                  annotation.getFinalAnnotation(),
                  annotation.getAnnotationType());
        } catch (Exception ex) {
          log.info(
              "Brat装换异常,异常标注id：{},对应的文本：{},标注的数据：{},异常信息内容：{}",
              annotation.getId(),
              annotation.getTerm(),
              annotation.getFinalAnnotation() + "--" + annotation.getReviewedAnnotation(),
              ex.getMessage());
          throw new InternalServerException("异常标注id：" + annotation.getId());
        }

        AnnotationCombineBratVO annotationCombineBratVO = new AnnotationCombineBratVO();
        BeanUtils.copyProperties(annotation, annotationCombineBratVO);
        annotationCombineBratVO.setFinalAnnotation(finalBratJson);
        annotationCombineBratVO.setReviewedAnnotation(reviewedBratJson);
        annotationBratVOList.add(annotationCombineBratVO);
      }
    }
    return annotationBratVOList;
  }

  /** 判断两个标注记录是否完全相同 */
  public static boolean compareAnnotation(String sourceAnnotation, String targetAnnotation) {
    AnnotationDocument sourceDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        sourceAnnotation == null ? "" : sourceAnnotation, sourceDocument);
    AnnotationDocument targetDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        targetAnnotation == null ? "" : targetAnnotation, targetDocument);
    if (sourceDocument.getEntities().size() != targetDocument.getEntities().size()) {
      return false;
    }
    if (sourceDocument.getEntities().size() == targetDocument.getEntities().size()
        && sourceDocument.getEntities().size() == 0) {
      return true;
    }
    if (sourceDocument.getEntities().size() > 0) {
      for (Entity entity : sourceDocument.getEntities()) {
        long num =
            targetDocument
                .getEntities()
                .stream()
                .filter(
                    x ->
                        x.getType().equals(entity.getType())
                            && x.getStart() == entity.getStart()
                            && x.getEnd() == entity.getEnd()
                            && x.getTerm().equals(entity.getTerm()))
                .count();
        if (num == 0) {
          return false;
        }
      }
    }
    if (sourceDocument.getRelationEntities().size()
        != targetDocument.getRelationEntities().size()) {
      return false;
    }
    if (sourceDocument.getRelationEntities().size() == targetDocument.getRelationEntities().size()
        && sourceDocument.getRelationEntities().size() == 0) {
      return true;
    }
    if (sourceDocument.getRelationEntities().size() > 0) {
      for (RelationEntity relationEntity : sourceDocument.getRelationEntities()) {
        long num =
            targetDocument
                .getRelationEntities()
                .stream()
                .filter(
                    x ->
                        x.getTargetTag().equals(relationEntity.getTargetTag())
                            && x.getSourceTag().equals(relationEntity.getSourceTag())
                            && x.getType().equals(relationEntity.getType()))
                .count();
        if (num == 0) {
          return false;
        }
      }
    }
    return true;
  }

  /** 获取当前字符串中的最大的标签 */
  public static String getRelationNewTag(String oldAnnotation) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annotationDocument);
    List<RelationEntity> relationEntityList = annotationDocument.getRelationEntities();
    int num =
        relationEntityList
            .stream()
            .map(x -> x.getTag().substring(1, x.getTag().length()))
            .map(Integer::valueOf)
            .max(Comparator.comparing(Function.identity()))
            .orElse(0);
    return "R" + (num + 1);
  }

  public static String getEntityNewTag(String oldAnnotation) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annotationDocument);
    List<Entity> entityList = annotationDocument.getEntities();
    int num =
        entityList
            .stream()
            .map(x -> x.getTag().substring(1, x.getTag().length()))
            .map(Integer::valueOf)
            .max(Comparator.comparing(Function.identity()))
            .orElse(0);
    return "T" + (num + 1);
  }

  /** 分词标注新增标注，过算法api，特殊处理 */
  public static String handleCrossAnnotation(
      String manualAnnotation, String newTerm, String newType, int startPosition, int endPosition) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    String newTag = getEntityNewTag(manualAnnotation);
    AnnotationDocumentManipulator.parseBratAnnotation(
        manualAnnotation == null ? "" : manualAnnotation, annotationDocument);
    annotationDocument.setEntities(
        annotationDocument
            .getEntities()
            .stream()
            .filter(
                x -> {
                  if (StringUtils.equals(x.getTerm(), newTerm)) {
                    return true;
                  }
                  return x.getEnd() <= startPosition || x.getStart() >= endPosition;
                })
            .collect(Collectors.toList()));
    annotationDocument
        .getEntities()
        .add(new Entity(newTag, startPosition, endPosition, newType, newTerm));
    return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
  }

  /** relation界面新增entities */
  public static String addRelationEntitiesAnnotation(
      String oldAnnotation, String type, int startPosition, int endPosition, String term) {
    String newTag = getEntityNewTag(oldAnnotation);
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annotationDocument);
    annotationDocument
        .getEntities()
        .add(new Entity(newTag, startPosition, endPosition, type, term));
    return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
  }

  /** 新增entities数组中的标注 */
  public static String addEntitiesAnnotation(
      String oldAnnotation, String type, int startPosition, int endPosition, String term) {
    String newTag = getEntityNewTag(oldAnnotation);
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annotationDocument);
    if (annotationDocument
        .getEntities()
        .stream()
        .anyMatch(
            x ->
                x.getTerm().equals(term)
                    && x.getType().equals(type)
                    && x.getStart() == startPosition
                    && x.getEnd() == endPosition)) {
      return oldAnnotation;
    } else {
      if (annotationDocument
          .getEntities()
          .stream()
          .anyMatch(
              x ->
                  x.getTerm().equals(term)
                      && x.getStart() == startPosition
                      && x.getEnd() == endPosition
                      && x.getType().equals("Sentence-end-unconfirmed")
                      && "Sentence-end".equals(type))) {
        annotationDocument
            .getEntities()
            .forEach(
                x -> {
                  if (x.getTerm().equals(term)
                      && x.getStart() == startPosition
                      && x.getEnd() == endPosition
                      && x.getType().equals("Sentence-end-unconfirmed")
                      && type.equals("Sentence-end")) {
                    x.setType("Sentence-end");
                  }
                });
      } else {
        annotationDocument
            .getEntities()
            .add(new Entity(newTag, startPosition, endPosition, type, term));
      }
    }
    return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
  }

  /** 删除entities数组中的标注，同时删除relations，(events/triggers待定) */
  public static String deleteEntitiesAnnotation(String oldAnnotation, String tag) {

    AnnotationDocument annoDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annoDocument);
    // 先删除指定的标签
    List<Entity> entityList =
        annoDocument
            .getEntities()
            .stream()
            .filter(x -> !x.getTag().equals(tag))
            .collect(Collectors.toList());
    annoDocument.setEntities(entityList);
    // 再删除与该标签相关联relation
    List<RelationEntity> relationEntityList =
        annoDocument
            .getRelationEntities()
            .stream()
            .filter(x -> !x.getTargetTag().equals(tag))
            .filter(x -> !x.getSourceTag().equals(tag))
            .collect(Collectors.toList());
    annoDocument.setRelationEntities(relationEntityList);
    // todo,后期加入events/triggers，同时删除events关联相关标签的关系
    return AnnotationDocumentManipulator.toBratAnnotations(annoDocument);
  }

  /** 更新entities数组中指定的标注 */
  public static String updateEntitiesAnnotation(String oldAnnotation, String tag, String newType) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annotationDocument);
    annotationDocument
        .getEntities()
        .forEach(
            x -> {
              if (x.getTag().equals(tag)) {
                x.setType(newType);
              }
            });
    return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
  }

  /** 新增relations数组中的标注 */
  public static String addRelationsAnnotation(
      String oldAnnotation, String sourceTag, String targetTag, String type) {
    String maxTag = getRelationNewTag(oldAnnotation);
    AnnotationDocument annoDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annoDocument);
    if (annoDocument
        .getRelationEntities()
        .stream()
        .anyMatch(
            x -> {
              if (x.getSourceTag().equals(sourceTag) && x.getTargetTag().equals(targetTag)) {
                return true;
              }

              return x.getSourceTag().equals(targetTag) && x.getTargetTag().equals(sourceTag);
            })) {
      return oldAnnotation;
    } else {
      annoDocument
          .getRelationEntities()
          .add(new RelationEntity(maxTag, type, sourceTag, targetTag, "source", "target"));
      return AnnotationDocumentManipulator.toBratAnnotations(annoDocument);
    }
  }

  /** 删除relations数组中的标注 */
  public static String deleteRelationsAnnotation(String oldAnnotation, String rTag) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annotationDocument);
    List<RelationEntity> relationEntityList =
        annotationDocument
            .getRelationEntities()
            .stream()
            .filter(x -> !x.getTag().equals(rTag))
            .collect(Collectors.toList());
    annotationDocument.setRelationEntities(relationEntityList);
    return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
  }

  /** 更新relations数组中指定的标注的类型 */
  public static String updateRelationAnnotation(String oldAnnotation, String rTag, String type) {
    AnnotationDocument annotationDocument = new AnnotationDocument();
    AnnotationDocumentManipulator.parseBratAnnotation(
        oldAnnotation == null ? "" : oldAnnotation, annotationDocument);
    annotationDocument
        .getRelationEntities()
        .forEach(
            x -> {
              if (x.getTag().equals(rTag)) {
                x.setType(type);
              }
            });
    if (annotationDocument.getRelationEntities().size() > 1
        && checkRelationRepetition(annotationDocument.getRelationEntities())) {
      return oldAnnotation;
    }
    return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
  }

  private static boolean checkRelationRepetition(List<RelationEntity> relationEntityList) {
    long count =
        IntStream.range(0, relationEntityList.size())
            .filter(
                i ->
                    relationEntityList
                        .stream()
                        .anyMatch(
                            x ->
                                !x.getTag().equals(relationEntityList.get(i).getTag())
                                    && x.getSourceTag()
                                        .equals(relationEntityList.get(i).getSourceTag())
                                    && x.getTargetTag()
                                        .equals(relationEntityList.get(i).getTargetTag())
                                    && x.getType().equals(relationEntityList.get(i).getType())))
            .count();
    return count > 0;
  }
}
