package cn.malgo.annotation.utils;

import cn.malgo.annotation.utils.entity.AnnotationDocument;
import cn.malgo.core.definition.Document;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.RelationEntity;
import cn.malgo.core.definition.utils.DocumentManipulator;
import cn.malgo.core.definition.utils.EntityManipulator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class AnnotationDocumentManipulator {
  /**
   * relations保存的文本格式 "{0}\t{1} {2} {3}\n";实例："R1 relation source:T1 target:T2" entities保存的文本格式
   * "{0}\t{1} {2} {3}\t{4}\n";实例："T1 body-structure 0 10 肺 " 将标注文本中的关于relation的标注提取出来并封装到relation对象
   * 以及entity的标注提取出来并封装到entity对象
   */
  public static void parseBratAnnotation(String anno, AnnotationDocument document) {
    final Document doc = new Document(document.getText());
    DocumentManipulator.parseBratAnnotations(anno, doc);
    document.setEntities(doc.getEntities());
    document.setRelationEntities(doc.getRelationEntities());
  }

  /** 直接转换成文本格式的数据，用来保存到数据库 */
  public static String toBratAnnotations(AnnotationDocument doc) {
    // 先读取entities数组中数据并转换为字符串
    final List<Entity> entities =
        doc.getEntities()
            .stream()
            .filter((e) -> !e.getType().equals("Sentence"))
            .collect(Collectors.toList());

    final List<String> bratEntities =
        entities
            .stream()
            .map(
                entity ->
                    String.format(
                        "%s\t%s %d %d\t%s",
                        entity.getTag(),
                        entity.getType(),
                        entity.getStart(),
                        entity.getEnd(),
                        entity.getTerm()))
            .collect(Collectors.toList());

    // 读取relations数组中的数据并转换为对应的字符串
    final List<RelationEntity> relationEntityList = doc.getRelationEntities();

    bratEntities.addAll(
        relationEntityList
            .stream()
            .map(
                relationEntity ->
                    String.format(
                        "%s\t%s %s %s",
                        relationEntity.getTag(),
                        relationEntity.getType(),
                        relationEntity.getSource() + ":" + relationEntity.getSourceTag(),
                        relationEntity.getTarget() + ":" + relationEntity.getTargetTag()))
            .collect(Collectors.toList()));

    return bratEntities.stream().collect(Collectors.joining("\n"));
  }

  /** 将document集合中的数据转换成前端可以展示的格式 */
  public static JSONObject toBratAjaxFormat(AnnotationDocument doc) {
    JSONObject bratJson = new JSONObject();
    String text = doc.getText();
    bratJson.put("text", text);
    List<Entity> sentences =
        doc.getEntities()
            .stream()
            .filter((e) -> e.getType().equals("Sentence"))
            .collect(Collectors.toList());
    bratJson.put(
        "sentence_offsets",
        sentences
            .stream()
            .map((e) -> Arrays.asList(new Integer[] {e.getStart(), e.getEnd()}))
            .collect(Collectors.toList()));
    List<Entity> tokens =
        EntityManipulator.getCoveredSmallestEntities(
            0,
            text.length(),
            doc.getEntities()
                .stream()
                .filter((e) -> !e.getType().equals("Sentence"))
                .collect(Collectors.toList()));
    if (tokens.size() == 0) {
      tokens =
          IntStream.range(0, text.length())
              .filter((i) -> text.substring(i, i + 1).equals("\n"))
              .mapToObj((i) -> new Entity((String) null, i, i + 1, (String) null, (String) null))
              .collect(Collectors.toList());
    }
    //        List<Integer> starts = (List) ((Set) tokens.stream().flatMap((e) ->
    //                Arrays.asList(new Integer[]{Integer.valueOf(e.getStart()),
    // Integer.valueOf(e.getEnd())}).stream()
    //        ).collect(Collectors.toSet())).stream().sorted().collect(Collectors.toList());
    //        List<Integer> ends = starts.stream().collect(Collectors.toList());
    //        starts.remove(starts.size() - 1);
    //        if (starts.size() != 0 && ((Integer) starts.get(0)).intValue() <= 0) {
    //            ends.remove(0);
    //        } else {
    //            starts.add(0, Integer.valueOf(0));
    //        }
    //
    //        if (ends.size() == 0 || ((Integer) ends.get(ends.size() - 1)).intValue() <
    // text.length()) {
    //            ends.add(Integer.valueOf(text.length()));
    //        }
    //
    //        List<List<Integer>> tokenOffsets = IntStream.range(0, starts.size()).mapToObj((i) ->
    //                Arrays.asList(new Integer[]{(Integer) starts.get(i), (Integer) ends.get(i)})
    //        ).collect(Collectors.toList());
    //        bratJson.put("token_offsets", tokenOffsets.stream().sorted(Comparator.comparing((e) ->
    //                (Integer) e.get(0)
    //        )).collect(Collectors.toList()));
    // 切割到每个字符
    bratJson.put(
        "token_offsets",
        IntStream.range(0, doc.getText().length())
            .mapToObj(i -> Arrays.asList(i, i + 1))
            .collect(Collectors.toList()));
    List<Entity> entities =
        doc.getEntities()
            .stream()
            .filter((e) -> !e.getType().equals("Sentence"))
            .collect(Collectors.toList());
    List<JSONArray> bratEntities =
        entities
            .stream()
            .map(
                entity -> {
                  JSONArray jsonEntity = new JSONArray();
                  jsonEntity.add(entity.getTag());
                  jsonEntity.add(entity.getType());
                  jsonEntity.add(new int[][] {{entity.getStart(), entity.getEnd()}});
                  return jsonEntity;
                })
            .collect(Collectors.toList());
    bratJson.put("entities", bratEntities);
    List<JSONArray> relationJson =
        doc.getRelationEntities()
            .stream()
            .map(
                relationEntity -> {
                  JSONArray jsonEntity = new JSONArray();
                  jsonEntity.add(relationEntity.getTag());
                  jsonEntity.add(relationEntity.getType());
                  jsonEntity.add(
                      Arrays.asList(
                          Arrays.asList("source", relationEntity.getSourceTag()),
                          Arrays.asList("target", relationEntity.getTargetTag())));
                  return jsonEntity;
                })
            .collect(Collectors.toList());
    bratJson.put("relations", relationJson);
    return bratJson;
  }
}
