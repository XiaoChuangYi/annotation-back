package com.malgo.utils;

import cn.malgo.core.definition.Entity;
import com.alibaba.fastjson.JSONObject;
import com.malgo.entity.AnnotationCombine;
import com.malgo.utils.entity.AnnotationDocument;
import com.malgo.utils.entity.RelationEntity;
import com.malgo.vo.AnnotationCombineBratVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by cjl on 2018/5/24.
 */
@Slf4j
public class AnnotationConvert {

    /**
     * 查询，将字符串形式的格式转换成前端可以渲染的jsonObject
     */
    public static JSONObject convertAnnotation2BratFormat(String text, String annotation) {
        AnnotationDocument annotationDocument = new AnnotationDocument(text);
        AnnotationDocumentManipulator.parseBratAnnotation(annotation==null?"":annotation, annotationDocument);
        JSONObject finalJsonObj = AnnotationDocumentManipulator.toBratAjaxFormat(annotationDocument);
        return finalJsonObj;
    }

    /**
     * 将分词标注数据装载到前端vo对象中
     */
    public static AnnotationCombineBratVO convert2AnnotationCombineBratVO(AnnotationCombine annotationCombine) {
        JSONObject finalBratJson = convertAnnotation2BratFormat(annotationCombine.getTerm(), annotationCombine.getFinalAnnotation());
        JSONObject reviewedBratJson=convertAnnotation2BratFormat(annotationCombine.getTerm(),annotationCombine.getReviewedAnnotation());
        AnnotationCombineBratVO annotationCombineBratVO=new AnnotationCombineBratVO();
        BeanUtils.copyProperties(annotationCombine, annotationCombineBratVO);
        annotationCombineBratVO.setFinalAnnotation(finalBratJson);
        annotationCombineBratVO.setReviewedAnnotation(reviewedBratJson);
        return annotationCombineBratVO;
    }

    /**
     * 批量将分词标注装载到前端vo对象中
     */
    public static List<AnnotationCombineBratVO> convert2AnnotationCombineBratVOList(List<AnnotationCombine> annotationCombineList) {
        List<AnnotationCombineBratVO> annotationBratVOList = new LinkedList<>();
        if (annotationCombineList.size() > 0) {
            for (AnnotationCombine annotation : annotationCombineList) {
                JSONObject finalBratJson = convertAnnotation2BratFormat(annotation.getTerm(), annotation.getFinalAnnotation());
                JSONObject reviewedBratJson=convertAnnotation2BratFormat(annotation.getTerm(),annotation.getReviewedAnnotation());
                AnnotationCombineBratVO annotationCombineBratVO=new AnnotationCombineBratVO();
                BeanUtils.copyProperties(annotation, annotationCombineBratVO);
                annotationCombineBratVO.setFinalAnnotation(finalBratJson);
                annotationCombineBratVO.setReviewedAnnotation(reviewedBratJson);
                annotationBratVOList.add(annotationCombineBratVO);
            }
        }
        return annotationBratVOList;
    }

//    /**
//     * 批量将分词标注装载到前端vo对象中
//     */
//    public static List<AnnotationWordPosExerciseBratVO> convert2AnnotationWordExerciseBratVOList(List<AnnotationWordExercise> annotationWordPosList) {
//        List<AnnotationWordPosExerciseBratVO> annotationBratVOList = new LinkedList<>();
//        if (annotationWordPosList.size() > 0) {
//            for (AnnotationWordExercise annotation : annotationWordPosList) {
//                JSONObject autoBratJson = convertAnnotation2BratFormat(annotation.getOriginText(), annotation.getAutoAnnotation());
//                JSONObject standardBratJson=convertAnnotation2BratFormat(annotation.getOriginText(),annotation.getStandardAnnotation());
//                AnnotationWordPosExerciseBratVO annotationBratVO = new AnnotationWordPosExerciseBratVO();
//                BeanUtils.copyProperties(annotation, annotationBratVO);
//                annotationBratVO.setAutoBratData(autoBratJson);
//                annotationBratVO.setStandardBratData(standardBratJson);
//                annotationBratVOList.add(annotationBratVO);
//            }
//        }
//        return annotationBratVOList;
//    }
//
//    /**
//     * 将分句标注数据装载到前端vo对象中
//     */
//    public static AnnotationSentenceBratVO convert2AnnotationSentenceBratVO(AnnotationSentence annotation) {
//        JSONObject bratJson = convertAnnotation2BratFormat(annotation.getOriginText(), annotation.getAnnotationText());
//        JSONObject finalBratJson = convertAnnotation2BratFormat(annotation.getOriginText(), annotation.getFinalAnnotationText());
//        AnnotationSentenceBratVO annotationSentenceBratVO = new AnnotationSentenceBratVO();
//        BeanUtils.copyProperties(annotation, annotationSentenceBratVO);
//        annotationSentenceBratVO.setBratData(bratJson);
//        annotationSentenceBratVO.setFinalBratData(finalBratJson);
//        return annotationSentenceBratVO;
//    }
//
//    /**
//     * 批量将分句标注装载到前端vo对象中
//     */
//    public static List<AnnotationSentenceBratVO> convert2AnnotationSentenceBratVOList(List<AnnotationSentence> annotationList) {
//        List<AnnotationSentenceBratVO> annotationBratVOList = new LinkedList<>();
//        for (AnnotationSentence annotation : annotationList) {
//            JSONObject bratJson = convertAnnotation2BratFormat(annotation.getOriginText(), annotation.getAnnotationText());
//            JSONObject finalBratJson = convertAnnotation2BratFormat(annotation.getOriginText(), annotation.getFinalAnnotationText());
//            AnnotationSentenceBratVO annotationSentenceBratVO = new AnnotationSentenceBratVO();
//            BeanUtils.copyProperties(annotation, annotationSentenceBratVO);
//            annotationSentenceBratVO.setBratData(bratJson);
//            annotationSentenceBratVO.setFinalBratData(finalBratJson);
//            annotationBratVOList.add(annotationSentenceBratVO);
//        }
//        return annotationBratVOList;
//    }
    /**
     * 分句分词练习标注，包括对应的标准答案集，表结构相似，可以复用作为统一的对象装载到对应的vo对象中
     * todo
     */

    /**
     * 获取当前字符串中的最大的标签
     *
     * @param oldAnnotation
     */
    public static String getRelationNewTag(String oldAnnotation) {
        AnnotationDocument annotationDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annotationDocument);
        List<RelationEntity> relationEntityList = annotationDocument.getRelationEntities();
        int num = relationEntityList.size() > 0 ? relationEntityList.stream()
                .map(x -> x.getTag().substring(1, x.getTag().length()))
                .map(s -> Integer.valueOf(s))
                .max(Comparator.comparing(Function.identity())).get().intValue() : 0;
        num++;
        return "R" + num;
    }

    public static String getEntityNewTag(String oldAnnotation) {
        AnnotationDocument annotationDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annotationDocument);
        List<Entity> entityList = annotationDocument.getEntities();
        int num = entityList.size() > 0 ? entityList.stream()
                .map(x -> x.getTag().substring(1, x.getTag().length()))
                .map(s -> Integer.valueOf(s))
                .max(Comparator.comparing(Function.identity())).get().intValue() : 0;
        num++;
        return "T" + num;
    }

    /**
     * 新增entities数组中的标注
     *
     * @param oldAnnotation
     * @param type
     * @param startPosition
     * @param endPosition
     * @param term
     */
    public static String addEntitesAnnotation(String oldAnnotation, String type, int startPosition, int endPosition, String term) {
        String newTag = getEntityNewTag(oldAnnotation);
        AnnotationDocument annotationDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annotationDocument);
        if (annotationDocument.getEntities().stream().filter(x -> x.getTerm().equals(term) && x.getType().equals(type)
                && x.getStart() == startPosition && x.getEnd() == endPosition)
                .count() > 0) {
            return oldAnnotation;
        } else {
            annotationDocument.getEntities().add(new Entity(newTag, startPosition, endPosition, type, term));
            return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
        }
    }

    /**
     * 删除entities数组中的标注，同时删除relations，(events/triggers待定)
     *
     * @param oldAnnotation
     * @param tag
     */
    public static String deleteEntitesAnnotation(String oldAnnotation, String tag) {

        AnnotationDocument annoDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annoDocument);
        //先删除指定的标签
        annoDocument.setEntities(annoDocument.getEntities().stream()
                .filter(x -> !x.getTag().equals(tag))
                .collect(Collectors.toList()));
        //再删除与该标签相关联relation
        annoDocument.setRelationEntities(annoDocument.getRelationEntities().stream()
                .filter(x -> !x.getTargetTag().equals(tag))
                .filter(x -> !x.getSourceTag().equals(tag))
                .collect(Collectors.toList()));
        //todo,后期加入events/triggers，同时删除events关联相关标签的关系
        return AnnotationDocumentManipulator.toBratAnnotations(annoDocument);
    }

    /**
     * 更新entities数组中指定的标注
     *
     * @param oldAnnotation
     * @param tag
     * @param newType
     */
    public static String updateEntitesAnnotation(String oldAnnotation, String tag, String newType) {
        AnnotationDocument annotationDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annotationDocument);
        annotationDocument.getEntities().stream()
                .forEach(x -> {
                    if (x.getTag().equals(tag)) {
                        x.setType(newType);
                    }
                });
        return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
    }

    /**
     * 新增relations数组中的标注
     *
     * @param oldAnnotation
     * @param sourceTag
     * @param targetTag
     * @param type
     */
    public static String addRelationsAnnotation(String oldAnnotation, String sourceTag, String targetTag, String type) {
        String maxTag = getRelationNewTag(oldAnnotation);
        AnnotationDocument annoDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annoDocument);
        if (annoDocument.getRelationEntities().stream()
                .filter(x -> x.getSourceTag().equals(sourceTag) && x.getTargetTag().equals(targetTag) && x.getType().equals(type))
                .count() > 0) {
            return oldAnnotation;
        } else {
            annoDocument.getRelationEntities().add(new RelationEntity(maxTag, type, sourceTag, targetTag, "source", "target"));
            return AnnotationDocumentManipulator.toBratAnnotations(annoDocument);
        }
    }

    /**
     * 删除relations数组中的标注
     *
     * @param oldAnnotation
     * @param rTag
     */
    public static String deleteRelationsAnnotation(String oldAnnotation, String rTag) {
        AnnotationDocument annotationDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annotationDocument);
        annotationDocument.setRelationEntities(annotationDocument.getRelationEntities().stream()
                .filter(x -> x.getTag().equals(rTag)).collect(Collectors.toList()));
        return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
    }

    /**
     * 更新relations数组中指定的标注的类型
     */
    public static String updateRelationAnnotation(String oldAnnotation, String rTag, String type) {
        AnnotationDocument annotationDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annotationDocument);
        annotationDocument.getRelationEntities().stream().forEach(x -> {
            if (x.getTag().equals(rTag)) {
                x.setType(type);
            }
        });
        if(annotationDocument.getRelationEntities().size()>1&&checkRelationRepetition(annotationDocument.getRelationEntities()))
            return oldAnnotation;
        return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
    }


    private static boolean checkRelationRepetition(List<RelationEntity> relationEntityList){
        long count = IntStream.range(0, relationEntityList.size())
                .filter(i ->
                        relationEntityList.stream()
                                .anyMatch(x -> x.getSourceTag().equals(relationEntityList.get(i).getSourceTag())
                                        && x.getTargetTag().equals(relationEntityList.get(i).getTargetTag())
                                        && x.getType().equals(relationEntityList.get(i).getType()))

                ).count();
        if(count>0)
            return true;
        return false;
    }


    /**
     * 更新relations数组中指定标注的sourceTag或者targetTag
     */
    public static String updateRelationTag(String oldAnnotation, String rTag, String sourceTag, String targetTag) {
        AnnotationDocument annotationDocument = new AnnotationDocument();
        AnnotationDocumentManipulator.parseBratAnnotation(oldAnnotation, annotationDocument);
        //加个判断，如果更新后的relation标签和之前的重复了，则不更新
        annotationDocument.getRelationEntities().stream().forEach(x -> {
            if (x.getTag().equals(rTag)) {
                if (StringUtils.isNotBlank(targetTag))
                    x.setTargetTag(targetTag);
                if (StringUtils.isNotBlank(sourceTag))
                    x.setSourceTag(sourceTag);
            }
        });
        if(annotationDocument.getRelationEntities().size()>1&&checkRelationRepetition(annotationDocument.getRelationEntities()))
            return oldAnnotation;
        return AnnotationDocumentManipulator.toBratAnnotations(annotationDocument);
    }
    /**
     * 后期加入events/triggers，另外新增相应的add/delete/update方法
     * todo
     */

}
