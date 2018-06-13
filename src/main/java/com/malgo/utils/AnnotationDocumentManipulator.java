package com.malgo.utils;

import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.utils.EntityManipulator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.malgo.utils.entity.AnnotationDocument;
import com.malgo.utils.entity.RelationEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by cjl on 2018/5/24.
 */
@Slf4j
public class AnnotationDocumentManipulator {


    /**
     * relations保存的文本格式 "{0}\t{1} {2} {3}\n";实例："R1  relation source:T1 target:T2"
     * entities保存的文本格式 "{0}\t{1} {2} {3}\t{4}\n";实例："T1 body-structure 0 10 肺 "
     * 将标注文本中的关于relation的标注提取出来并封装到relation对象
     * 以及entity的标注提取出来并封装到entity对象
     */
    public static void parseBratAnnotation(String anno, AnnotationDocument document) {
        List<String> records = Arrays.asList(anno.split("\n"));
        //封装DB中的relation格式的数据到实体集合
        List<RelationEntity> relationEntities =
                records.stream()
                        .filter((s) -> s.startsWith("R"))
                        .map((s) -> {
                            String[] tabs = s.split("\\s");
                            if (tabs.length != 4) {
                                log.warn("invalid annotation text: {}, annotation: {}", document.getText(), anno);
                                throw new IllegalArgumentException("invalid annotation text: " + document.getText() + ", annotation: " + anno);
                            }

                            String tag = tabs[0];
                            String type = tabs[1];
                            String[] sourceGroups = tabs[2].split(":");
                            String source = sourceGroups[0];
                            String sourceTag = sourceGroups[1];
                            String[] targetGroups = tabs[3].split(":");
                            String target = targetGroups[0];
                            String targetTag = targetGroups[1];
                            return new RelationEntity(tag, type, sourceTag, targetTag, source, target);
                        }).collect(Collectors.toList());
        document.setRelationEntities(relationEntities);
        //封装DB中的entity格式的数据到实体集合
        List<Entity> entities = records.stream().filter((s) ->
                s.startsWith("T")
        ).map((s) -> {
            final String[] tabs = s.split("\\s");

            if (tabs.length != 5) {
                log.warn("invalid annotation text: {}, annotation: {}", document.getText(), anno);
                throw new IllegalArgumentException("invalid annotation text: " + document.getText() + ", annotation: " + anno);
            }

            return new Entity(tabs[0], Integer.parseInt(tabs[2]), Integer.parseInt(tabs[3]), tabs[1], tabs[4]);
        }).collect(Collectors.toList());
        document.setEntities(entities);
    }


    /**
     * 直接转换成文本格式的数据，用来保存到数据库
     */
    public static String toBratAnnotations(AnnotationDocument doc) {
        //先读取entities数组中数据并转换为字符串
        List<Entity> entities = doc.getEntities().stream().filter((e) ->
                !e.getType().equals("Sentence")
        ).collect(Collectors.toList());
        List<String> bratEntities = IntStream.range(0, entities.size()).mapToObj((i) -> {
            Entity e = entities.get(i);
            return String.format("%s\t%s %d %d\t%s",
                    new Object[]{e.getTag(), e.getType(), Integer.valueOf(e.getStart()),
                            Integer.valueOf(e.getEnd()), e.getTerm()});
        }).collect(Collectors.toList());
        //读取relations数组中的数据并转换为对应的字符串
        List<RelationEntity> relationEntityList = doc.getRelationEntities();
        bratEntities.addAll(IntStream.range(0, relationEntityList.size()).mapToObj((i) -> {
            RelationEntity e = relationEntityList.get(i);
            return String.format("%s\t%s %s %s",
                    new Object[]{e.getTag(), e.getType(), e.getSource() + ":" + e.getSourceTag(),
                            e.getTarget() + ":" + e.getTargetTag()});
        }).collect(Collectors.toList()));

        return bratEntities.stream().collect(Collectors.joining("\n"));
    }

    /**
     * 将document集合中的数据转换成前端可以展示的格式
     */
    public static JSONObject toBratAjaxFormat(AnnotationDocument doc) {
        JSONObject bratJson = new JSONObject();
        String text = doc.getText();
        bratJson.put("text", text);
        List<Entity> sentences = doc.getEntities().stream().filter((e) ->
                e.getType().equals("Sentence")
        ).collect(Collectors.toList());
        bratJson.put("sentence_offsets", sentences.stream().map((e) ->
                Arrays.asList(new Integer[]{Integer.valueOf(e.getStart()), Integer.valueOf(e.getEnd())})
        ).collect(Collectors.toList()));
        List<Entity> tokens = EntityManipulator.getCoveredSmallestEntities(0, text.length(),
                (List) doc.getEntities().stream().filter((e) ->
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
//
//        List<List<Integer>> tokenOffsets = IntStream.range(0, starts.size()).mapToObj((i) ->
//                Arrays.asList(new Integer[]{(Integer) starts.get(i), (Integer) ends.get(i)})
//        ).collect(Collectors.toList());
//        bratJson.put("token_offsets", tokenOffsets.stream().sorted(Comparator.comparing((e) ->
//                (Integer) e.get(0)
//        )).collect(Collectors.toList()));
        //切割到每个字符
        bratJson.put("token_offsets", IntStream.range(0, doc.getText().length()).mapToObj(i ->
                Arrays.asList(i, i + 1)
        ).collect(Collectors.toList()));
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
        List<JSONArray> relationJson = IntStream.range(0, doc.getRelationEntities().size())
                .mapToObj((i) -> {
                    RelationEntity e = doc.getRelationEntities().get(i);
                    JSONArray jsonEntity = new JSONArray();
                    jsonEntity.add(e.getTag());
                    jsonEntity.add(e.getType());
                    jsonEntity.add(Arrays.asList(Arrays.asList("source", e.getSourceTag()),
                            Arrays.asList("target", e.getTargetTag())));
                    return jsonEntity;
                }).collect(Collectors.toList());
        bratJson.put("relations", relationJson);
        return bratJson;
    }

}
