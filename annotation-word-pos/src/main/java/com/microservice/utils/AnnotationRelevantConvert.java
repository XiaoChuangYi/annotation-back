package com.microservice.utils;

import cn.malgo.core.definition.BratConst;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.utils.EntityManipulator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microservice.dataAccessLayer.entity.AnnotationWordPos;
import com.microservice.dto.AnnoDocument;
import com.microservice.dto.RelationEntity;
import com.microservice.result.AnnotationBratVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by cjl on 2018/5/23.
 */
public class AnnotationRelevantConvert {


    /**
     * 批量将标注数据部分字段装换成前端可以渲染的数据格式
     */
    public static List<AnnotationBratVO> convert2AnnotationBratVOList(List<AnnotationWordPos> annotationWordPosList) {
        List<AnnotationBratVO> annotationBratVOList = new LinkedList<>();
        if (annotationWordPosList.size() > 0) {
            for (AnnotationWordPos annotation : annotationWordPosList) {
                JSONObject bratJson = toConvertAnnotation2BratFormat(annotation.getTerm(), annotation.getFinalAnnotation());
                AnnotationBratVO annotationBratVO = new AnnotationBratVO();
                BeanUtils.copyProperties(annotation, annotationBratVO);
                annotationBratVO.setBratData(bratJson);
                annotationBratVO.setNewTerms(JSONArray.parseArray(annotation.getNewTerms()));
                annotationBratVOList.add(annotationBratVO);
            }
        }
        return annotationBratVOList;
    }

    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static AnnotationBratVO convert2AnnotationBratVO(AnnotationWordPos annotation) {
        JSONObject bratJson = toConvertAnnotation2BratFormat(annotation.getTerm(), annotation.getFinalAnnotation());
        AnnotationBratVO annotationBratVO = new AnnotationBratVO();
        BeanUtils.copyProperties(annotation, annotationBratVO);
        annotationBratVO.setBratData(bratJson);
        annotationBratVO.setNewTerms(JSONArray.parseArray(annotation.getNewTerms()));
        return annotationBratVO;
    }

    /**
     * 查询，将字符串形式的格式转换成前端可以渲染的jsonObject
     */
    public static JSONObject toConvertAnnotation2BratFormat(String text, String annotation) {
        AnnoDocument annoDocument = new AnnoDocument(text);
        parseBratAnnotation(annotation, annoDocument);
        JSONObject finalJsonObj = toBratAjaxFormat(annoDocument);
        finalJsonObj.put(BratConst.TOKEN_OFFSET, IntStream.range(0, annoDocument.getText().length())
                .mapToObj(i -> Arrays.asList(i, i+1))
                .collect(Collectors.toList()));
        return finalJsonObj;
    }

    /**
     * relation保存的文本格式 "{0}\t{1} {2} {3}\n";实例："R1  relation source:T1 target:T2"
     * entities
     * 将标注文本中的关于relation的标注提取出来并封装到relation对象
     */
    public static void parseBratAnnotation(String anno, AnnoDocument document) {
        List<String> records = Arrays.asList(anno.split("\n"));
        //封装DB中的relation格式的数据到实体集合
        List<RelationEntity> relationEntities = records.stream().filter((s) -> s.startsWith("R")
        ).map((s) -> {
            String[] tabs = s.split("\t");
            String tag = tabs[0];
            String[] spaces = tabs[1].split(" ");
            String type = spaces[0];
            String[] sourceGroups = spaces[1].split(":");
            String source = sourceGroups[0];
            String sourceTag = sourceGroups[1];
            String[] targetGroups = spaces[2].split(":");
            String target = targetGroups[0];
            String targetTag = targetGroups[1];
            return new RelationEntity(tag, type, sourceTag, targetTag, source, target);
        }).collect(Collectors.toList());
        document.setRelationEntities(relationEntities);
        //封装DB中的entity格式的数据到实体集合
        List<Entity> entities = records.stream().filter((s) ->
                s.startsWith("T")
        ).map((s) -> {
            String[] tabs = s.split("\t");
            String[] spaces = tabs[1].split(" ");
            String term = tabs[2];
            String tag = tabs[0];
            String type = spaces[0];
            int start = Integer.parseInt(spaces[1]);
            int end = Integer.parseInt(spaces[2]);
            return new Entity(tag, start, end, type, term);
        }).collect(Collectors.toList());
        document.setEntities(entities);
    }

    /**
     * 获取relation的新的R前缀标签
     */
    public static String getNewRelationTag(String oldAnnotation) {
        AnnoDocument annoDocument = new AnnoDocument();
        parseBratAnnotation(oldAnnotation, annoDocument);
        List<RelationEntity> entityList = annoDocument.getRelationEntities();
        int num = entityList.size() > 0 ? entityList.stream()
                .map(x -> x.getTag().substring(1, x.getTag().length()))
                .map(s -> Integer.valueOf(s))
                .max(Comparator.comparing(Function.identity())).get().intValue() : 0;
        num++;
        return "R" + num;
    }

    /**
     * 新增relation
     */
    public static String addRelationAnnotation(String oldAnnotation, String sourceTag, String targetTag, String type) {
        String maxTag = getNewRelationTag(oldAnnotation);
        AnnoDocument annoDocument = new AnnoDocument();
        parseBratAnnotation(oldAnnotation, annoDocument);
        if (annoDocument.getRelationEntities().stream()
                .filter(x -> x.getSourceTag().equals(sourceTag) && x.getTargetTag().equals(targetTag) && x.getType().equals(type))
                .count() > 0) {
            return oldAnnotation;
        } else {
            annoDocument.getRelationEntities().add(new RelationEntity(maxTag, type, sourceTag, targetTag, "source", "target"));
            return toBratAnnotations(annoDocument);
        }
    }

    /**
     * 删除relation
     */
    public static String deleteRelationAnnotation(String oldAnnotation, String relationTag) {
        AnnoDocument annoDocument = new AnnoDocument();
        parseBratAnnotation(oldAnnotation, annoDocument);
        annoDocument.setRelationEntities(annoDocument.getRelationEntities().stream()
                .filter(x -> !x.getTag().equals(relationTag))
                .collect(Collectors.toList()));
        return toBratAnnotations(annoDocument);
    }

    /**
     * 更新relation的type
     */
    public static String updateRelationAnnotation(String oldAnnotation, String rTag, String newType) {
        AnnoDocument annoDocument = new AnnoDocument();
        parseBratAnnotation(oldAnnotation, annoDocument);
        annoDocument.getRelationEntities().stream().forEach(x -> {
            if (x.getTag().equals(rTag)) {
                x.setType(newType);
            }
        });

        if(annoDocument.getRelationEntities().size()>1&&checkRelationRepetition(annoDocument.getRelationEntities()))
            return oldAnnotation;
        return toBratAnnotations(annoDocument);
    }
    private static boolean checkRelationRepetition(List<RelationEntity> relationEntityList){
        long count = IntStream.range(0, relationEntityList.size())
                .filter(i ->
                        relationEntityList.stream()
                                .anyMatch(x ->!x.getTag().equals(relationEntityList.get(i).getTag())&&x.getSourceTag().equals(relationEntityList.get(i).getSourceTag())
                                        && x.getTargetTag().equals(relationEntityList.get(i).getTargetTag())
                                        && x.getType().equals(relationEntityList.get(i).getType()))

                ).count();
        if(count>0)
            return true;
        return false;
    }

    /**
     * 更新relation的sourceTag或者targetTag
     */
    public static String updateRelationTag(String oldAnnotation, String rTag, String sourceTag, String targetTag) {
        AnnoDocument annoDocument = new AnnoDocument();
        parseBratAnnotation(oldAnnotation, annoDocument);
        annoDocument.getRelationEntities().stream().forEach(x -> {
            if (x.getTag().equals(rTag)) {
                if (StringUtils.isNotBlank(sourceTag))
                    x.setSourceTag(sourceTag);
                if (StringUtils.isNotBlank(targetTag))
                    x.setTargetTag(targetTag);
            }
        });
        if(annoDocument.getRelationEntities().size()>1&&checkRelationRepetition(annoDocument.getRelationEntities()))
            return oldAnnotation;
        return toBratAnnotations(annoDocument);
    }


    /**
     * 直接转换成文本格式的数据，用来保存到数据库
     */
    public static String toBratAnnotations(AnnoDocument doc) {

        //先读取entities数组中数据并转换为字符串
        List<Entity> entities = doc.getEntities().stream().filter((e) ->
                !e.getType().equals("Sentence")
        ).collect(Collectors.toList());
        List<String> bratEntities = IntStream.range(0, entities.size()).mapToObj((i) -> {
            Entity e = entities.get(i);
            return String.format("%s\t%s %d %d\t%s", new Object[]{e.getTag(), e.getType(), Integer.valueOf(e.getStart()), Integer.valueOf(e.getEnd()), e.getTerm()});
        }).collect(Collectors.toList());
        //读取relations数组中的数据并转换为对应的字符串
        List<RelationEntity> relationEntityList = doc.getRelationEntities();
        bratEntities.addAll(IntStream.range(0, relationEntityList.size()).mapToObj((i) -> {
            RelationEntity e = relationEntityList.get(i);
            return String.format("%s\t%s %s %s", new Object[]{e.getTag(), e.getType(), e.getSource() + ":" + e.getSourceTag(), e.getTarget() + ":" + e.getTargetTag()});
        }).collect(Collectors.toList()));

        return bratEntities.stream().collect(Collectors.joining("\n"));
    }

    /**
     * 将doc集合中的数据转换成前端可以展示的格式
     */
    public static JSONObject toBratAjaxFormat(AnnoDocument doc) {
        JSONObject bratJson = new JSONObject();
        String text = doc.getText();
        bratJson.put("text", text);
        List<Entity> sentences = doc.getEntities().stream().filter((e) ->
                e.getType().equals("Sentence")
        ).collect(Collectors.toList());
        bratJson.put("sentence_offsets", sentences.stream().map((e) ->
                Arrays.asList(new Integer[]{Integer.valueOf(e.getStart()), Integer.valueOf(e.getEnd())})
        ).collect(Collectors.toList()));
        List<Entity> tokens = EntityManipulator.getCoveredSmallestEntities(0, text.length(), (List) doc.getEntities().stream().filter((e) ->
                !e.getType().equals("Sentence")
        ).collect(Collectors.toList()));
        if (tokens.size() == 0) {
            tokens = IntStream.range(0, text.length()).filter((i) ->
                    text.substring(i, i + 1).equals("\n")
            ).mapToObj((i) ->
                    new Entity((String) null, i, i + 1, (String) null, (String) null)
            ).collect(Collectors.toList());
        }
//        List<Integer> starts = (List) ((Set) tokens.stream().flatMap((e) ->
//                Arrays.asList(new Integer[]{Integer.valueOf(e.getStart()), Integer.valueOf(e.getEnd())}).stream()
//        ).collect(Collectors.toSet())).stream().sorted().collect(Collectors.toList());
//        List<Integer> ends = starts.stream().collect(Collectors.toList());
//        starts.remove(starts.size() - 1);
//        if (starts.size() != 0 && ((Integer) starts.get(0)).intValue() <= 0) {
//            ends.remove(0);
//        } else {
//            starts.add(0, Integer.valueOf(0));
//        }
//
//        if (ends.size() == 0 || ((Integer) ends.get(ends.size() - 1)).intValue() < text.length()) {
//            ends.add(Integer.valueOf(text.length()));
//        }

//        List<List<Integer>> tokenOffsets = IntStream.range(0, starts.size()).mapToObj((i) ->
//                Arrays.asList(new Integer[]{(Integer) starts.get(i), (Integer) ends.get(i)})
//        ).collect(Collectors.toList());
//        bratJson.put("token_offsets", tokenOffsets.stream().sorted(Comparator.comparing((e) ->
//                (Integer) e.get(0)
//        )).collect(Collectors.toList()));
        List<Entity> entities = doc.getEntities().stream().filter((e) ->
                !e.getType().equals("Sentence")
        ).collect(Collectors.toList());
        List<JSONArray> bratEntities = IntStream.range(0, entities.size()).mapToObj((i) -> {
            Entity e = entities.get(i);
            JSONArray jsonEntity = new JSONArray();
            jsonEntity.add(e.getTag());
            jsonEntity.add(e.getType());
            jsonEntity.add(new int[][]{{e.getStart(), e.getEnd()}});
            return jsonEntity;
        }).collect(Collectors.toList());
        bratJson.put("entities", bratEntities);
        List<JSONArray> relationJson = IntStream.range(0, doc.getRelationEntities().size()).mapToObj((i) -> {
            RelationEntity e = doc.getRelationEntities().get(i);
            JSONArray jsonEntity = new JSONArray();
            jsonEntity.add(e.getTag());
            jsonEntity.add(e.getType());
            jsonEntity.add(Arrays.asList(Arrays.asList("source", e.getSourceTag()), Arrays.asList("target", e.getTargetTag())));
            return jsonEntity;
        }).collect(Collectors.toList());
        bratJson.put("relations", relationJson);
        return bratJson;
    }

}
