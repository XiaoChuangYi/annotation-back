package com.microservice.utils;

import cn.malgo.core.definition.BratConst;
import cn.malgo.core.definition.Document;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.utils.DocumentManipulator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microservice.dataAccessLayer.entity.AnnotationSentence;
import com.microservice.result.AnnotationSentenceBratVO;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by cjl on 2018/4/16.
 */
public class AnnotationConvert {


    public static Logger logger = Logger.getLogger(AnnotationConvert.class);


    /**
     * 文本内容转化为brat格式的且数据返回格式是jsonObject
     * @param originTerm
     * @param annotationData
     */
    public static JSONObject convertToBratFormat(String originTerm,String annotationData){
        Document document=new Document(originTerm,null);
        DocumentManipulator.parseBratAnnotations(annotationData,document);
        JSONObject jsonObject=DocumentManipulator.toBratAjaxFormat(document);
        List<Integer> endPositionList=document.getEntities().stream().map(x->x.getEnd()).sorted().collect(Collectors.toList());

        List<Integer> startPositionList = endPositionList.stream().collect(Collectors.toList());
        if (endPositionList.size() == 0 || endPositionList.get(endPositionList.size()-1) < document.getText().length()){
            endPositionList.add(document.getText().length());
        }else{
            startPositionList.remove(startPositionList.size()-1);
        }
        if (startPositionList.size() == 0 || startPositionList.get(0) != 0){

            startPositionList.add(0,0);
        }

        jsonObject.put(BratConst.SENTENCE_OFFSET, IntStream.range(0, endPositionList.size())
                .mapToObj(i -> Arrays.asList(startPositionList.get(i), endPositionList.get(i)))
                .collect(Collectors.toList()));

        jsonObject.put(BratConst.TOKEN_OFFSET, IntStream.range(0, document.getText().length())
                .mapToObj(i -> Arrays.asList(i, i+1))
                .collect(Collectors.toList()));
        return jsonObject;

    }

    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static List<AnnotationSentenceBratVO> convert2AnnotationBratVOList(List<AnnotationSentence> annotationList){
        List<AnnotationSentenceBratVO> annotationBratVOList=new LinkedList<>();
        for(AnnotationSentence annotation:annotationList){
            JSONObject bratJson=convertToBratFormat(annotation.getOriginText(),annotation.getAnnotationText());
            AnnotationSentenceBratVO annotationSentenceBratVO=new AnnotationSentenceBratVO();
            BeanUtils.copyProperties(annotation,annotationSentenceBratVO);
            annotationSentenceBratVO.setBratData(bratJson);
            annotationBratVOList.add(annotationSentenceBratVO);
        }
        return annotationBratVOList;
    }

    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static AnnotationSentenceBratVO convert2AnnotationBratVO(AnnotationSentence annotation){
        JSONObject bratJson=convertToBratFormat(annotation.getOriginText(),annotation.getAnnotationText());
        AnnotationSentenceBratVO annotationSentenceBratVO=new AnnotationSentenceBratVO();
        BeanUtils.copyProperties(annotation,annotationSentenceBratVO);
        annotationSentenceBratVO.setBratData(bratJson);
        return annotationSentenceBratVO;
    }

    /**
     * 通过lambda添加新的单位标注从而构建新标注
     * @param oldAnnotation
     * @param newType
     * @param newStart
     * @param newEnd
     * @param newTerm
     * @return
     */
    public static String addUnitAnnotation(String oldAnnotation,String newType,int newStart,
                                                   int newEnd,String newTerm){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        List<Entity> entityList=document.getEntities();
        int num=entityList.size()>0 ? entityList.stream()
            .map(x->x.getTag().substring(1,x.getTag().length()))
            .map(s -> Integer.valueOf(s))
            .max(Comparator.comparing(Function.identity())).get().intValue()+1:1;
        String newTag= "T"+num;

        if(document.getEntities().stream().filter(x->x.getTerm().equals(newTerm)&&x.getType().equals(newType)
                &&x.getStart()==newStart&&x.getEnd()==newEnd)
                .count()>0){
            return oldAnnotation; // 已存在同样的标注就不添加 TODO 有隐患.
        }else{
            document.getEntities().add(new Entity(newTag,newStart,newEnd,newType,newTerm));
            return DocumentManipulator.toBratAnnotations(document);
        }
    }

    /**
     *根据tag删除标注中指定的单位标注
     * @param tag
     * @param oldAnnotation
     */
    public static String deleteUnitAnnotation(String oldAnnotation,String tag){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        document.setEntities(document.getEntities().stream()
                .filter(x->!x.getTag().equals(tag))
                .collect(Collectors.toList()));
        logger.info("删除后的标注："+ JSONArray.parseArray(JSON.toJSONString(document.getEntities())));
        return DocumentManipulator.toBratAnnotations(document);
    }

    /**
     * 修改tag对应的标注类型
     * @param oldAnnotation
     * @param tag
     * @return
     */
    public static String changeUnitAnnotation(String oldAnnotation,String tag, String type){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        document.getEntities().stream().filter(x -> x.getTag().equals(tag)).forEach(x -> x.setType(type));
        return DocumentManipulator.toBratAnnotations(document);
    }
}
