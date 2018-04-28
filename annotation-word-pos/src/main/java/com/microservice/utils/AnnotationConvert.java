package com.microservice.utils;

import cn.malgo.common.LogUtil;
import cn.malgo.core.definition.Document;
import cn.malgo.core.definition.Entity;
import cn.malgo.core.definition.utils.DocumentManipulator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.microservice.apiserver.vo.TermTypeVO;
import com.microservice.dataAccessLayer.entity.Annotation;
import com.microservice.result.AnnotationBratVO;
import com.microservice.vo.AtomicTermAnnotation;
import com.microservice.vo.CombineAtomicTerm;
import com.microservice.vo.TermAnnotationModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by cjl on 2018/3/29.
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
        return DocumentManipulator.toBratAjaxFormat(document);
    }
    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static List<AnnotationBratVO>  convert2AnnotationBratVOList(List<Annotation> annotationList){
        List<AnnotationBratVO> annotationBratVOList=new LinkedList<>();
        for(Annotation annotation:annotationList){
            JSONObject bratJson=convertToBratFormat(annotation.getTerm(),annotation.getFinalAnnotation());
            AnnotationBratVO annotationBratVO=new AnnotationBratVO();
            BeanUtils.copyProperties(annotation,annotationBratVO);
            annotationBratVO.setBratData(bratJson);
            annotationBratVO.setNewTerms(JSONArray.parseArray(annotation.getNewTerms()));
            annotationBratVOList.add(annotationBratVO);
        }
        return annotationBratVOList;
    }

    /**
     * 将标注数据部分字段转换成前端可以渲染的数据格式
     */
    public static AnnotationBratVO convert2AnnotationBratVO(Annotation annotation){
            JSONObject bratJson=convertToBratFormat(annotation.getTerm(),annotation.getFinalAnnotation());
            AnnotationBratVO annotationBratVO=new AnnotationBratVO();
            BeanUtils.copyProperties(annotation,annotationBratVO);
            annotationBratVO.setBratData(bratJson);
            annotationBratVO.setNewTerms(JSONArray.parseArray(annotation.getNewTerms()));
            return annotationBratVO;
    }

    /**
     * 通过lambda获取指定标注的新的Tag标签
     */
    public static String getNewTagByLambda(String annotation){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(annotation,document);
        List<Entity> entityList=document.getEntities();
        int num=entityList.size()>0?entityList.stream()
                .map(x->x.getTag().substring(1,x.getTag().length()))
                .map(s -> Integer.valueOf(s))
                .max(Comparator.comparing(Function.identity())).get().intValue():1;
        num++;
        return "T"+num;
    }


    public static String handleCrossAnnotation(String manualAnnotation,String newTerm,String newType,int startPosition,int endPosition){
        Document document=new Document("",new LinkedList<>());
        String newTag=getNewTagByLambda(manualAnnotation);
        DocumentManipulator.parseBratAnnotations(manualAnnotation==null?"":manualAnnotation,document);
        document.setEntities(document.getEntities().stream().filter(x->!x.getTerm().contains(newTerm)).collect(Collectors.toList()));
        document.getEntities().add(new Entity(newTag,startPosition,endPosition,newType,newTerm));
        return DocumentManipulator.toBratAnnotations(document);
    }

    /**
     * 判断新增的单位标注文本和已有的单位文本是否有交叉
     */
    public static  boolean isCrossAnnotation(String oldAnnotation,String newTerm){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        String [] newTermArr=newTerm.split("");
        long num=document.getEntities().stream().filter(x->{
            int count=0;
            for (int i=0;i<newTermArr.length;i++) {
                if(x.getTerm().contains(newTermArr[i])){
                    count++;
                    break;
                }
            }
            if(count>0) {
                return true;
            }else {
                return false;
            }
        }).count();
        if(num>0)
            return true;
        return false;
    }

    /**
     * 通过lambda添加新的单位标注从而构建新标注
     * oldAnnotation
     * @param newType
     * @param newStart
     * @param newEnd
     * @param newTerm
     * @return
     */
    public static String addUnitAnnotationByLambda(String oldAnnotation,String newType,int newStart,
                                                   int newEnd,String newTerm){
        String newTag=getNewTagByLambda(oldAnnotation);
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        if(document.getEntities().stream().filter(x->x.getTerm().equals(newTerm)&&x.getType().equals(newType)
                &&x.getStart()==newStart&&x.getEnd()==newEnd)
                .count()>0){
            return oldAnnotation;
        }else{
            document.getEntities().add(new Entity(newTag,newStart,newEnd,newType,newTerm));
            return DocumentManipulator.toBratAnnotations(document);
        }
    }

    /**
     * 通过lambda实现，更新原先标注中指定的单位标注类型
     * @param oldAnnotation
     * @param  oldType
     * @param newType
     * @param tag
     */
    public static String updateUnitAnnotationTypeByLambda(String oldAnnotation,String oldType,String newType,String tag){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation==null?"":oldAnnotation,document);
        if(document.getEntities().size()>0)
            document.getEntities().stream().filter(x->x.getTag().equals(tag))
                    .forEach(e->e.setType(newType));
        logger.info("更新后的标注："+JSONArray.parseArray(JSON.toJSONString(document.getEntities())));
        return DocumentManipulator.toBratAnnotations(document);
    }

    /**
     *根据tag删除标注中指定的单位标注
     * @param tag
     * @param oldAnnotation
     */
    public static String deleteUnitAnnotationByLambda(String oldAnnotation,String tag){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        document.setEntities(document.getEntities().stream()
                .filter(x->!x.getTag().equals(tag))
                .collect(Collectors.toList()));
        logger.info("删除后的标注："+JSONArray.parseArray(JSON.toJSONString(document.getEntities())));
        return DocumentManipulator.toBratAnnotations(document);
    }

    /**
     *
     *
     */
    public static String convertAnnotation2Str(List<Entity> entityList){
        Document document=new Document("",entityList);
        return DocumentManipulator.toBratAnnotations(document);
    }

    /**
     *
     *
     */
    public static List<Entity> getUnitAnnotationList(String oldAnnotation){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        return document.getEntities();
    }


    /**
     * 通过lambda实现细分标注中的单位标注
     * @param atomicTermAnnotationList
     * @param oldAnnotation
     * @param originText
     * @return
     */
    public static String getAnnotationAfterDivideUnitAnnotation(List<AtomicTermAnnotation> atomicTermAnnotationList,String oldAnnotation,String originText){
        //新增细分单位标注
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        Entity replaceEntity=null;
        if((replaceEntity=document.getEntities().stream().filter(x->x.getTerm().equals(originText)).findFirst().get())!=null){
            for(AtomicTermAnnotation x:atomicTermAnnotationList){
                oldAnnotation=DocumentManipulator.toBratAnnotations(document);
                document.getEntities().add(new Entity(getNewTagByLambda(oldAnnotation),
                        replaceEntity.getStart() + Integer.valueOf(x.getStartPosition()),
                        replaceEntity.getStart() + Integer.valueOf(x.getEndPosition()),
                        x.getAnnotationType(),
                        x.getText()));
            }
            //删除被拆分标注
            document.setEntities(document.getEntities().stream().filter(x->!x.getTerm().equals(originText)).collect(Collectors.toList()));
            return DocumentManipulator.toBratAnnotations(document);
        }
        return oldAnnotation;
    }

    /**
     * 通过lambda实现合并单位标注
     * @param combineAtomicTermList
     * @param annotation
     * @param term
     * @param type
     * @return
     */
    public static String getAnnotationAfterCombineAnnotationByLambda(List<CombineAtomicTerm> combineAtomicTermList,
                                                                     Annotation annotation,String term,String type){
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(annotation.getFinalAnnotation(),document);
        int endPosition=0,startPosition=0;
        List<Integer> startPositionList=new LinkedList<>();
        List<Integer> endPositionList=new LinkedList<>();
        for(CombineAtomicTerm o:combineAtomicTermList) {
            endPosition= document.getEntities().stream()
                    .filter(x -> x.getTerm().toUpperCase().equals(o.getTerm().toUpperCase())
                            && x.getType().equals(o.getType()))
                    .map(s -> s.getEnd()).findFirst().get().intValue();
            endPositionList.add(endPosition);
            startPosition=document.getEntities().stream()
                    .filter(x->x.getTerm().toUpperCase().equals(o.getTerm().toUpperCase())
                            && x.getType().equals(o.getType()))
                    .map(s->s.getStart()).findFirst().get().intValue();
            startPositionList.add(startPosition);
            //先删除合并的单位标注
            document.setEntities(document.getEntities().stream()
                    .filter(x->!(x.getTerm().toUpperCase().equals(o.getTerm().toUpperCase())&&x.getType().equals(o.getType())))
                    .collect(Collectors.toList()));
        }
        logger.info("startPosition："+startPositionList.stream().mapToInt(x->x.intValue()).summaryStatistics().getMin()+"；endPosition："+ endPositionList.stream().mapToInt(x->x.intValue()).summaryStatistics().getMax());
        return addUnitAnnotationByLambda(DocumentManipulator.toBratAnnotations(document),type,
                startPositionList.stream().mapToInt(x->x.intValue()).summaryStatistics().getMin()+"",
                endPositionList.stream().mapToInt(x->x.intValue()).summaryStatistics().getMax()+"",
                term);
    }

    /**
     * 通过lambda添加新的单位标注从而构建新标注
     * oldAnnotation
     * @param newType
     * @param newStart
     * @param newEnd
     * @param newTerm
     * @return
     */
    public static String addUnitAnnotationByLambda(String oldAnnotation,String newType,String newStart,
                                                   String newEnd,String newTerm){
        String newTag=getNewTagByLambda(oldAnnotation);
        Document document=new Document("",new LinkedList<>());
        DocumentManipulator.parseBratAnnotations(oldAnnotation,document);
        if(document.getEntities().stream().filter(x->x.getTerm().equals(newTerm)&&x.getType().equals(newType)
                &&x.getStart()==Integer.valueOf(newStart)&&x.getEnd()==Integer.valueOf(newEnd))
                .count()>0){
            return oldAnnotation;
        }else{
            document.getEntities().add(new Entity(newTag,Integer.valueOf(newStart),Integer.valueOf(newEnd),newType,newTerm));
            return DocumentManipulator.toBratAnnotations(document);
        }
    }


    /**
     * 在原有新词列表中,增加新词
     * @param oldTerms
     * @param newTerm
     * @param newTermType
     * @return
     */
    public static String addNewTerm(String oldTerms, String newTerm, String newTermType) {
        JSONArray termArray;
        if (StringUtils.isBlank(oldTerms)) {
            termArray = new JSONArray();
        } else {
            termArray = JSONArray.parseArray(oldTerms);
        }

        //检查是否已经存在要添加的新词,如果已经存在,直接返回
        for (Object object : termArray) {
            String oldTermType = JSONObject.parseObject(object.toString()).getString(newTerm);
            if (newTermType.equals(oldTermType)) {
                return oldTerms;
            }
        }

        //将新词添加到原有新词列表中
        JSONObject newTermObject = new JSONObject();
        newTermObject.put(newTerm, newTermType);
        termArray.add(newTermObject);
        return JSONArray.toJSONString(termArray);
    }
    /**
     * 根据tag在手工标注中查找标注内容和标注类型
     * @param annotation
     * @param tag
     * @return
     */
    public static TermTypeVO getTermTypeVOByTag(String annotation, String tag) {

        String[] lines = annotation.split("\n");
        for (String line : lines) {
            if (line.contains(tag)) {
                String[] lineElements = line.split("\t");
                String term = lineElements[2];
                String termType = lineElements[1].split(" ")[0];
                TermTypeVO termTypeVO = new TermTypeVO();
                termTypeVO.setType(termType);
                termTypeVO.setTerm(term);
                return termTypeVO;
            }
        }
        return null;
    }
    /**
     * 从原有的新词列表中删除新词
     * @param oldTerms
     * @param newTerm
     * @param newTermType
     * @return
     */
    public static String deleteNewTerm(String oldTerms, String newTerm, String newTermType) {
        if (StringUtils.isBlank(oldTerms)) {
            return "";
        }

        JSONArray termArray = JSONArray.parseArray(oldTerms);

        JSONArray newTermArray = new JSONArray();

        //检查是否存在待删除的新词,如果存在,不再构建到新的新词列表中
        for (Object object : termArray) {
            String oldTermType = JSONObject.parseObject(object.toString()).getString(newTerm);
            if (!newTermType.equals(oldTermType)) {
                newTermArray.add(object);
            }
        }

        return JSONArray.toJSONString(newTermArray);
    }

    public static List<TermAnnotationModel> convertAnnotationModelList(String text) {

//        LogUtil.debug(logger,"待转换文本:"+text);
//        System.out.println("待转换文本:"+text);

        List<TermAnnotationModel> termAnnotationModelList = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return termAnnotationModelList;
        }

        String[] lines = text.split("\n");
        for (String line : lines) {
            if(!StringUtils.isNotBlank(line))
                continue;
            TermAnnotationModel termAnnotationModel = new TermAnnotationModel();

            String[] elements = line.split("\t");
//            System.out.println("elements:"+JSONArray.toJSONString(elements));
            termAnnotationModel.setTag(elements[0]);
            termAnnotationModel.setTerm(elements[2]);

            String[] structElement = elements[1].split(" ");
            termAnnotationModel.setType(structElement[0]);
            termAnnotationModel.setStartPosition(Integer.valueOf(structElement[1]));
            termAnnotationModel.setEndPosition(Integer.valueOf(structElement[2]));

            termAnnotationModelList.add(termAnnotationModel);
        }

        LogUtil.debug(logger,"转换结果:"+JSONObject.toJSONString(termAnnotationModelList));


        return termAnnotationModelList;
    }
}
